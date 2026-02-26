package com.forum.service;

import com.forum.base.BasedMessage;
import com.forum.dto.RegisterRequest;
import com.forum.dto.UserResponse;
import com.forum.dto.users.ChangePasswordRequest;
import com.forum.dto.users.UpdateProfileImage;
import com.forum.dto.users.UserDetailResponse;
import com.forum.dto.users.UserUpdateRequest;
import com.forum.entity.User;
import com.forum.exception.ResourceNotFoundException;
import com.forum.exception.ResourceAlreadyExistsException;
import com.forum.mapper.UserMapper;
import com.forum.media.MediaService;
import com.forum.media.dto.MediaResponse;
import com.forum.repository.UserRepository;
import com.forum.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MediaService mediaService;

    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToDTO(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToDTO(user);
    }

    public User getUserEntityById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public UserResponse createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + registerRequest.getEmail());
        }
        if (userRepository.existsByDisplayName(registerRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Display name already exists: " + registerRequest.getUsername());
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new ResourceNotFoundException("Confirm password not match");
        }

        User user = new User();
        user.setDisplayName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setConfirmPassword(registerRequest.getConfirmPassword());
        user.setCreationDate(LocalDateTime.now());
        user.setLastAccessDate(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

//    public UserResponse updateUser(Integer userId, UserUpdateRequest userDTO) {
//        User user = getUserEntityById(userId);
//
//        if (userDTO.getDisplayName() != null && !user.getDisplayName().equals(userDTO.getDisplayName())) {
//            if (userRepository.existsByDisplayName(userDTO.getDisplayName())) {
//                throw new ResourceAlreadyExistsException("Display name already exists: " + userDTO.getDisplayName());
//            }
//            user.setDisplayName(userDTO.getDisplayName());
//        }
//
//        if (userDTO.getEmail() != null && !user.getEmail().equals(userDTO.getEmail())) {
//            if (userRepository.existsByEmail(userDTO.getEmail())) {
//                throw new ResourceAlreadyExistsException("Email already exists: " + userDTO.getEmail());
//            }
//            user.setEmail(userDTO.getEmail());
//        }
//
//        User updatedUser = userRepository.save(user);
//        return mapToDTO(updatedUser);
//    }

    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public List<UserResponse> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getTopUsersByReputation(Integer minReputation) {
        return userRepository.findTopUsersByReputation(minReputation).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void updateLastAccessDate(Integer userId) {
        User user = getUserEntityById(userId);
        user.setLastAccessDate(LocalDateTime.now());
        userRepository.save(user);
    }

    public void incrementReputation(Integer userId, Integer points) {
        User user = getUserEntityById(userId);
        user.setReputation(user.getReputation() + points);
        userRepository.save(user);
    }

    public void incrementUpVotes(Integer userId) {
        User user = getUserEntityById(userId);
        user.setUpVotes(user.getUpVotes() + 1);
        userRepository.save(user);
    }

    public void incrementDownVotes(Integer userId) {
        User user = getUserEntityById(userId);
        user.setDownVotes(user.getDownVotes() + 1);
        userRepository.save(user);
    }

    private UserResponse mapToDTO(User user) {
        return new UserResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getReputation(),
                user.getViews(),
                user.getUpVotes(),
                user.getDownVotes(),
                user.getCreationDate(),
                user.getLastAccessDate()
        );
    }


    public UserDetailResponse getUserInfo(String email) {
        return userMapper.toUserDetailResponse(
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email: " + email + " not found!")
                        )
        );
    }

//    change password logic here

    public void updatePassword(String email, ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        // verify old password matches
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        // verify new password and confirm password match
        if (!request.newPassword().equals(request.confirmedNewPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "New password and confirm password do not match");
        }

        // prevent reusing the same password
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "New password must be different from old password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void updateUserInfo(String email, @Valid UserUpdateRequest userUpdateRequest) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        user.setDisplayName(userUpdateRequest.username());
        user.setBio(userUpdateRequest.bio());

        userRepository.save(user);
    }

    public void updateProfileImage(String email, MultipartFile file) {

        // validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only image files are allowed");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        MediaResponse response = mediaService.uploadSingle(file, "profile-images");

        user.setProfileImage(response.uri());
        userRepository.save(user);
    }
}

