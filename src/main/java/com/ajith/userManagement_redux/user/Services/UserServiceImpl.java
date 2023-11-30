package com.ajith.userManagement_redux.user.Services;

import com.ajith.userManagement_redux.Config.JwtService;
import com.ajith.userManagement_redux.user.Exceptions.CustomAuthenticationException;
import com.ajith.userManagement_redux.user.Requests.UserDetailsUpdateRequest;
import com.ajith.userManagement_redux.user.Response.UserDetailsResponse;
import com.ajith.userManagement_redux.user.User;
import com.ajith.userManagement_redux.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    public UserDetailsResponse getUserDetails(String token) {
        try {
             String userEmail = jwtService.extractUsername(token);
            String username = jwtService.extractUsername(token);
            Optional<User> userOptional = userRepository.findByEmail(username);

            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();

                // Create UserDetailsResponse without including the password
                UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
                userDetailsResponse.setFirstname(existingUser.getFirstname ());
                userDetailsResponse.setLastname(existingUser.getLastname ());
                userDetailsResponse.setEmail(existingUser.getEmail());

                return userDetailsResponse;
            } else {
                // User not found
                throw new CustomAuthenticationException ("User not found");
            }
        } catch (Exception e) {
            // Handle exceptions and log the error
            e.printStackTrace();
            throw new CustomAuthenticationException ("Error fetching user details");
        }
    }






    @Override
    public void updateUserDetails(String token, UserDetailsUpdateRequest userDetailsUpdateRequest) {
        try {
            String username = jwtService.extractUsername(token.substring ( 7 ));
            Optional<User> optionalUser = userRepository.findByEmail(username);

            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                // Update user details
                existingUser.setFirstname (userDetailsUpdateRequest.getFirstname ());
                existingUser.setLastname (userDetailsUpdateRequest.getLastname ());
                existingUser.setEmail(userDetailsUpdateRequest.getEmail());

                if(userDetailsUpdateRequest.getPassword () != null){
                    Optional<String> newPassword = userDetailsUpdateRequest.getPassword ();
                    if(newPassword.isPresent ())
                     existingUser.setPassword( newPassword.get ( ) );
                }else {
                    existingUser.setPassword (existingUser.getPassword (  ));
                }

                userRepository.save(existingUser);
            } else {
                // User not found
                throw new CustomAuthenticationException ("User not found");
            }
        } catch (Exception e) {
            // Handle exceptions and log the error
            e.printStackTrace();
            throw new CustomAuthenticationException ("Error updating user details");
        }
    }


    @Override
    public void updateProfilePicture (String token, MultipartFile file) {

    }
}
