package com.ajith.userManagement_redux.auth;


import com.ajith.userManagement_redux.auth.Reponse.AuthenticationResponse;
import com.ajith.userManagement_redux.auth.Request.AuthenticationRequest;
import com.ajith.userManagement_redux.auth.Request.RegisterRequest;
import com.ajith.userManagement_redux.user.Exceptions.CustomAuthenticationException;
import com.ajith.userManagement_redux.user.Requests.UserDetailsUpdateRequest;
import com.ajith.userManagement_redux.user.Response.UserDetailsResponse;
import com.ajith.userManagement_redux.user.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping ("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserService userService;

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

    @GetMapping ("/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader ("Authorization") String token) {
        try {

            UserDetailsResponse userDetails = userService.getUserDetails ( token.substring ( 7 ) );
            return ResponseEntity.ok ( userDetails );
        } catch (CustomAuthenticationException e) {
            return ResponseEntity.status ( HttpStatus.UNAUTHORIZED ).body ( e.getMessage ( ) );
        }
    }


    @PostMapping ("/update-user")
    public ResponseEntity<?> updateUserDetails(
            @RequestHeader ("Authorization") String token,
            @RequestBody UserDetailsUpdateRequest userDetailsUpdateRequest
    ){
        try {
            userService.updateUserDetails(token, userDetailsUpdateRequest);
            return ResponseEntity.ok("User details updated successfully");
        } catch (CustomAuthenticationException e) {
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @PostMapping("/addProfilePic")
    public ResponseEntity<?> addProfilePic(
            @RequestHeader("Authorization") String token,
            @RequestParam ("file") MultipartFile file
    ) {
        try {
            System.out.println (file);
            userService.updateProfilePicture(token, file);
            return ResponseEntity.ok("Profile picture updated successfully");
        } catch (CustomAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
