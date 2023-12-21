package com.ajith.userManagement_redux.auth;

import com.ajith.userManagement_redux.Config.JwtService;
import com.ajith.userManagement_redux.auth.Reponse.AuthenticationResponse;
import com.ajith.userManagement_redux.auth.Request.AuthenticationRequest;
import com.ajith.userManagement_redux.auth.Request.RegisterRequest;
import com.ajith.userManagement_redux.user.Exceptions.CustomAuthenticationException;
import com.ajith.userManagement_redux.user.Role;
import com.ajith.userManagement_redux.user.Services.UserService;
import com.ajith.userManagement_redux.user.User;
import com.ajith.userManagement_redux.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    private  final JwtService jwtService;
    private  final AuthenticationManager authenticationManager;
    private final UserService userService;
    public AuthenticationResponse register (RegisterRequest request) {
        System.out.println (request );
        var user = User.builder ( )
                .firstname ( request.getFirstname () )
                .lastname ( request.getLastname () )
                .email ( request.getEmail () )
                .phonenumber ( request.getPhonenumber () )
                .password (passwordEncoder.encode (request.getPassword ()))
                .role ( Role.USER )

                .build ();
        userRepository.save ( user );
        var jwtToken = jwtService.generateToken ( user );
        return AuthenticationResponse.builder ( )
                .token(jwtToken)
                .build ( );
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (user.getIsActive ()) {
                throw new UserBlockedException ("User is blocked");
            }
            var jwtToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (BadCredentialsException e) {

            throw new BadCredentialsException ("Password is Wrong");
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found");
        }
    }

}
