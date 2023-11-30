package com.ajith.userManagement_redux.auth;


import com.ajith.userManagement_redux.auth.Reponse.AuthenticationResponse;
import com.ajith.userManagement_redux.auth.Request.AuthenticationRequest;
import com.ajith.userManagement_redux.auth.Request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity< AuthenticationResponse > register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok ( service.register( request) );
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok ( service.authenticate( request) );
    }
}
