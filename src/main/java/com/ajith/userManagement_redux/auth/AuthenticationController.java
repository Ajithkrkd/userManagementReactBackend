package com.ajith.userManagement_redux.auth;


import com.ajith.userManagement_redux.auth.Reponse.AuthenticationResponse;
import com.ajith.userManagement_redux.auth.Request.AuthenticationRequest;
import com.ajith.userManagement_redux.auth.Request.RegisterRequest;
import com.ajith.userManagement_redux.user.Exceptions.CustomAuthenticationException;
import com.ajith.userManagement_redux.user.Requests.UserDetailsUpdateRequest;
import com.ajith.userManagement_redux.user.Response.UserDetailsResponse;
import com.ajith.userManagement_redux.user.Services.UserService;
import com.ajith.userManagement_redux.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping ("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserService userService;
    private  final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        try {
            boolean existEmail = userService.isEmailExist(request.getEmail());

            if (existEmail) {
                // Email already exists, return an error response
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(AuthenticationResponse.builder()
                                .error("Email already exists")
                                .build());
            }

            AuthenticationResponse response = service.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthenticationResponse.builder()
                            .error("An error occurred during registration")
                            .build());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity < ? > register(
            @RequestBody AuthenticationRequest request
    ){
        try {
            AuthenticationResponse response = service.authenticate(request);
            return ResponseEntity.ok(response);
        }
        catch (UsernameNotFoundException e) {
            return ResponseEntity.status(403).body("User not found");
        }
        catch (UserBlockedException e) {
            return ResponseEntity.status(403).body("user is blocked");
        }

        catch (BadCredentialsException e) {
            return ResponseEntity.status(403).body("Invalid email or password");
        }  catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
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
            System.out.println (token);
            String fileName = userService.updateProfilePicture(token, file);
            return ResponseEntity.ok("Profile picture updated successfully");
        } catch (CustomAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
