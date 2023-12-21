package com.ajith.userManagement_redux.user.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private String profileImagePath;

}
