package com.forum.controller;

import com.forum.base.BasedMessage;
import com.forum.dto.UserResponse;
import com.forum.dto.UserUpdateRequest;
import com.forum.dto.users.ChangePasswordRequest;
import com.forum.dto.users.UserDetailResponse;
import com.forum.security.CustomUserDetails;
import com.forum.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.security.Principal;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;


    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }




    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve user information by email address")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

//    @PutMapping("/{userId}")
//    @PreAuthorize("hasRole('USER')")
//    @SecurityRequirement(name = "Bearer Authentication")
//    @Operation(summary = "Update user", description = "Update user information (authenticated users only)")
//    @ApiResponse(responseCode = "200", description = "User updated successfully")
//    @ApiResponse(responseCode = "401", description = "Unauthorized")
//    @ApiResponse(responseCode = "404", description = "User not found")
//    public ResponseEntity<UserResponse> updateUser(
//            @PathVariable Integer userId,
//            @Valid @RequestBody UserUpdateRequest userDTO) {
//        UserResponse updatedUser = userService.updateUser(userId, userDTO);
//        return ResponseEntity.ok(updatedUser);
//    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete user", description = "Delete user account (authenticated users only)")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


//    @GetMapping("/me")
//    public UserResponse getCurrentUser(Authentication authentication) {
//
//        Jwt jwt = (Jwt) authentication.getPrincipal();
//        String email = jwt.getClaim("iss");
//
//        return userService.getUserInfo(email);
//
//    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailResponse> getMe() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return ResponseEntity.ok(userService.getUserInfo(userDetails.getUsername()));
    }

    @PutMapping("/update-user")
    public ResponseEntity<BasedMessage> updateUserInfo( @Valid @RequestBody com.forum.dto.users.UserUpdateRequest userUpdateRequest) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        userService.updateUserInfo(userDetails.getEmail(), userUpdateRequest);
        return ResponseEntity.ok(new BasedMessage("User updated successfully"));
    }

    @PutMapping("/update-password")
    public ResponseEntity<BasedMessage> updateNewPassword(@Valid @RequestBody ChangePasswordRequest request) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        userService.updatePassword(userDetails.getEmail(),request);

        return ResponseEntity.ok(new BasedMessage("Password updated successfully"));

    }

    @PutMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BasedMessage> updateProfileImage(
            @RequestPart(name = "file") MultipartFile file) {

        // validate file is not empty
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must not be empty");
        }

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        userService.updateProfileImage(userDetails.getEmail(), file);

        return ResponseEntity.ok(new BasedMessage("Profile image has been updated"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search for users by display name or email")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String query) {
        List<UserResponse> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

//    @GetMapping("/top-reputation")
//    @Operation(summary = "Get top users", description = "Get users with highest reputation scores")
//    @ApiResponse(responseCode = "200", description = "Users returned")
//    public ResponseEntity<List<UserResponse>> getTopUsersByReputation(
//            @RequestParam(defaultValue = "0") Integer minReputation) {
//        List<UserResponse> users = userService.getTopUsersByReputation(minReputation);
//        return ResponseEntity.ok(users);
//    }
}
