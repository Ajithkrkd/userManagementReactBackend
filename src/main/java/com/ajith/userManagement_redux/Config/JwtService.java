package com.ajith.userManagement_redux.Config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "ebPOOal7zbI/R9kr7ueSpDOPLwGAHvgJ3CSZgAby7la72GQJAB86YH1tHBJ31Ofs";
    public String extractUsername (String token) {
        System.out.println ("from extract username" );
        return extractClaim (token, Claims::getSubject);
    }


    public <T>T extractClaim (String token, Function<Claims,T>claimsResolver) {
        final Claims claims = extractAllClaims ( token );
        return claimsResolver.apply ( claims );
    }

    public  String generateToken(UserDetails userDetails){
        return generateToken ( new HashMap <> () ,userDetails );
    }

    public  boolean isTokenValid(String token ,UserDetails userDetails){
        System.out.println ("hai from validation" );
        String username = extractUsername ( token );
        return (username.equals ( userDetails.getUsername() ) ) && isTokenExpired(token);
    }

    private boolean isTokenExpired (String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration (String token) {
        return extractClaim ( token ,Claims::getExpiration );
    }

    public String generateToken(Map <String ,Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder ()
                .setClaims ( extraClaims )
                .setSubject ( userDetails.getUsername () )
                .setIssuedAt ( new Date (System.currentTimeMillis ()) )
                .setExpiration ( new Date (System.currentTimeMillis ()+ 1000 * 60 * 24) )
                .signWith ( getSigningKey (), SignatureAlgorithm.HS256 )
                .compact ();
    }
    private Claims extractAllClaims (String token) {
        return Jwts
                .parserBuilder ()
                .setSigningKey ( getSigningKey() )
                .build ()
                .parseClaimsJws ( token )
                .getBody ();
    }

    private Key getSigningKey ( ) {
        System.out.println ( "get key" );
        byte[] keyBytes = Decoders.BASE64.decode (SECRET_KEY);
        return Keys.hmacShaKeyFor (keyBytes);
    }
}
