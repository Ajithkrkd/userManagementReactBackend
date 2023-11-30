package com.ajith.userManagement_redux.user.Requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsUpdateRequest {
    private String firstname;
    private String lastname;
    private String email;
    private Optional <String> password;
}
