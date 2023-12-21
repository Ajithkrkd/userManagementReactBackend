package com.ajith.userManagement_redux.user.Services;

import com.ajith.userManagement_redux.Config.JwtService;
import com.ajith.userManagement_redux.user.Exceptions.CustomAuthenticationException;
import com.ajith.userManagement_redux.user.Requests.UserDetailsUpdateRequest;
import com.ajith.userManagement_redux.user.Response.UserDetailsResponse;
import com.ajith.userManagement_redux.user.Role;
import com.ajith.userManagement_redux.user.User;
import com.ajith.userManagement_redux.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    public  final PasswordEncoder passwordEncoder;

    @Override
    public UserDetailsResponse getUserDetails(String token) {
        try {
             String userEmail = jwtService.extractUsername(token);
            String username = jwtService.extractUsername(token);
            Optional<User> userOptional = userRepository.findByEmail(username);

            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();


                UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
                if (existingUser.getProfileImagePath() != null && !existingUser.getProfileImagePath().isEmpty()) {
                    userDetailsResponse.setProfileImagePath (existingUser.getProfileImagePath ());
                }
                userDetailsResponse.setFirstname(existingUser.getFirstname ());
                userDetailsResponse.setLastname(existingUser.getLastname ());
                userDetailsResponse.setEmail(existingUser.getEmail());
                userDetailsResponse.setPhonenumber (existingUser.getPhonenumber ());


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
                existingUser.setPhonenumber ( userDetailsUpdateRequest.getPhonenumber ());

                if(userDetailsUpdateRequest.getPassword ().isPresent ()){
                    System.out.println (userDetailsUpdateRequest.getPassword () +"ajith krkd");
                    String newPassword = userDetailsUpdateRequest.getPassword ().get();

                        System.out.println (newPassword +"ajith lrd ajfpoasjkfpskjafdp");
                        existingUser.setPassword( passwordEncoder.encode ( newPassword ) );


                }else {
                    System.out.println (existingUser.getPassword () +"ajith" );
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
    public String updateProfilePicture(String token, MultipartFile imageFile) {
        String userEmail = jwtService.extractUsername(token.substring(7));
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            try {
                String fileName = uploadImageAndSaveImagePathToUser(imageFile);
                user.setProfileImagePath ("/uploads"+"/"+fileName);
                userRepository.save(user);
                return fileName;
            } catch (IOException e) {

                throw new RuntimeException("Failed to upload profile picture", e);
            }
        } else {

            throw new RuntimeException("User not found for the given email: " + userEmail);
        }
    }

    public void blockUser(Integer userId) {
        try {
            Optional < User > optionalUser = userRepository.findById ( userId );
            if ( optionalUser.isPresent ( ) ) {
                User user = optionalUser.get ( );
                user.setIsActive ( !user.getIsActive () );
                userRepository.save(user);
            } else {
                throw new UsernameNotFoundException ( "User not found with id: " + userId );
            }
        } catch (Exception e) {
            throw new ResponseStatusException ( HttpStatus.INTERNAL_SERVER_ERROR, "Error blocking user", e);
        }
    }

    @Override
    public void updateRole (Integer userId) {
        try {
            Optional < User > optionalUser = userRepository.findById ( userId );
            if ( optionalUser.isPresent ( ) ) {
                Role role1 = Role.USER;
                Role role2 = Role.ADMIN;
                User user = optionalUser.get ( );

                if(user.getRole () == role1){
                    user.setRole (role2 );
                }else{
                    user.setRole (role1 );
                }

                userRepository.save(user);
            } else {
                throw new UsernameNotFoundException ( "User not found with id: " + userId );
            }
        } catch (Exception e) {
            throw new ResponseStatusException ( HttpStatus.INTERNAL_SERVER_ERROR, "Error blocking user", e);
        }
    }

    @Override
    public boolean isEmailExist (String email) {



        return userRepository.existsByEmail ( email );

    }

    private String uploadImageAndSaveImagePathToUser(MultipartFile imageFile) throws IOException {
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = imageFile.getOriginalFilename();
        String filePath = uploadDir + "/" + fileName;
        Path path = Paths.get(filePath);

        try {
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Profile picture is uploaded: " + fileName);
            return fileName;
        } catch (IOException e) {
            // Handle the file copy exception
            throw new IOException("Failed to copy profile picture file", e);
        }
    }

}
