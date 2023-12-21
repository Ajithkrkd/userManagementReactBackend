package com.ajith.userManagement_redux.user.Services;

import com.ajith.userManagement_redux.user.Requests.UserDetailsUpdateRequest;
import com.ajith.userManagement_redux.user.Response.UserDetailsResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService{
    UserDetailsResponse getUserDetails (String token);

    void updateUserDetails (String token, UserDetailsUpdateRequest userDetailsUpdateRequest);

    String updateProfilePicture (String token, MultipartFile file);

    void blockUser (Integer userId);

    void updateRole (Integer userId);

    boolean isEmailExist (String email);
}
