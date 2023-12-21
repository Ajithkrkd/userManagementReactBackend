package com.ajith.userManagement_redux.admin;

import com.ajith.userManagement_redux.user.Services.UserService;
import com.ajith.userManagement_redux.user.User;
import com.ajith.userManagement_redux.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    @GetMapping("/users/all")
    public List < User > getUsers() {
        return  userRepository.findAll ();
    }

    @PostMapping("/block/{userId}")
    public ResponseEntity<String> blockUser(@PathVariable Integer userId) {
        try {
            userService.blockUser(userId);
            return ResponseEntity.ok("User blocked successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error blocking user");
        }
    }

    @PostMapping("/updateRole/{userId}")
    public ResponseEntity<String> updateRole(@PathVariable Integer userId) {
        try {
            userService.updateRole(userId);
            return ResponseEntity.ok(" updated Role successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating  Role");
        }
    }
}
