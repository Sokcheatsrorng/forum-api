package com.forum.service;

import com.forum.dto.AuthResponse;
import com.forum.dto.LoginRequest;
import com.forum.dto.RegisterRequest;
import com.forum.entity.User;
import com.forum.exception.AuthenticationFailedException;
import com.forum.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        userService.createUser(request);
        return login(new LoginRequest(request.getEmail(), request.getPassword()));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userService.getUserEntityByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationFailedException("Invalid email or password");
        }

        String token = jwtProvider.generateToken(user.getEmail());

        userService.updateLastAccessDate(user.getId());

        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getDisplayName()
        );
    }
}
