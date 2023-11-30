package com.ajith.userManagement_redux.user.Services;

import com.ajith.userManagement_redux.user.Requests.UserDetailsUpdateRequest;
import com.ajith.userManagement_redux.user.Response.UserDetailsResponse;
import com.ajith.userManagement_redux.user.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService{
    UserDetailsResponse getUserDetails (String token);

    void updateUserDetails (String token, UserDetailsUpdateRequest userDetailsUpdateRequest);

    void updateProfilePicture (String token, MultipartFile file);
}
