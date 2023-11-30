package com.ajith.userManagement_redux.auth;

import com.ajith.userManagement_redux.Config.JwtService;
import com.ajith.userManagement_redux.auth.Reponse.AuthenticationResponse;
import com.ajith.userManagement_redux.auth.Request.AuthenticationRequest;
import com.ajith.userManagement_redux.auth.Request.RegisterRequest;
import com.ajith.userManagement_redux.user.Role;
import com.ajith.userManagement_redux.user.User;
import com.ajith.userManagement_redux.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    private  final JwtService jwtService;
    private  final AuthenticationManager authenticationManager;
    public AuthenticationResponse register (RegisterRequest request) {
        var user = User.builder ( )
                .firstname ( request.getFirstname () )
                .lastName ( request.getLastName () )
                .email ( request.getEmail () )
                .password (passwordEncoder.encode (request.getPassword ()))
                .role ( Role.USER )
                .build ();
        userRepository.save ( user );
        var jwtToken = jwtService.generateToken ( user );
        return AuthenticationResponse.builder ( )
                .token(jwtToken)
                .build ( );
    }

    public AuthenticationResponse authenticate (AuthenticationRequest request) {
        authenticationManager.authenticate (
                new UsernamePasswordAuthenticationToken (
                        request.getEmail (),
                        request.getPassword ()

                )
        );
      var user = userRepository.findByEmail ( request.getEmail() ).orElseThrow ();
        var jwtToken = jwtService.generateToken ( user );
        return AuthenticationResponse.builder ( )
                .token(jwtToken)
                .build ( );
    }
}
