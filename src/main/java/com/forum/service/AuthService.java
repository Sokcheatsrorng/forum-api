package com.forum.service;

import com.forum.dto.AuthResponse;
import com.forum.dto.ActionResponse;
import com.forum.dto.EmailVerificationResponse;
import com.forum.dto.ForgotPasswordRequest;
import com.forum.dto.LoginRequest;
import com.forum.dto.RegisterRequest;
import com.forum.dto.RegisterResponse;
import com.forum.dto.ResendVerificationRequest;
import com.forum.dto.ResetPasswordRequest;
import com.forum.dto.VerifyEmailRequest;
import com.forum.entity.AccountTokenType;
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
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AccountTokenService accountTokenService;
    private final EmailService emailService;

    public RegisterResponse register(RegisterRequest request) {
        userService.createUser(request);
        User user = userService.getUserEntityByEmail(request.email());
        sendVerificationEmail(user);
        return new RegisterResponse(
                "Registration successful. Check your email to verify your account before logging in.",
                user.getId(), user.getEmail(), true);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userService.getUserEntityByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationFailedException("Invalid email or password");
        }
        if (!user.isEmailVerified()) {
            throw new AuthenticationFailedException("Please verify your email before logging in");
        }

        return issueTokens(user);
    }

    public AuthResponse refresh(String refreshToken) {
        return issueTokens(refreshTokenService.consume(refreshToken));
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    public EmailVerificationResponse verifyEmail(VerifyEmailRequest request) {
        User user = accountTokenService.consume(request.token(), AccountTokenType.EMAIL_VERIFICATION);
        user.setEmailVerified(true);
        return new EmailVerificationResponse("Email verified. You can now log in.", user.getEmail(), true);
    }

    public ActionResponse resendVerification(ResendVerificationRequest request) {
        userService.findUserByEmail(request.email())
                .filter(user -> !user.isEmailVerified())
                .ifPresent(this::sendVerificationEmail);
        return new ActionResponse("If the account exists and is not verified, a verification email has been sent.");
    }

    public ActionResponse forgotPassword(ForgotPasswordRequest request) {
        userService.findUserByEmail(request.email()).ifPresent(user -> {
            String token = accountTokenService.create(user, AccountTokenType.PASSWORD_RESET, 30);
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        });
        return new ActionResponse("If an account exists for that email, a password reset link has been sent.");
    }

    public ActionResponse resetPassword(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }
        User user = accountTokenService.consume(request.token(), AccountTokenType.PASSWORD_RESET);
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        refreshTokenService.revokeAll(user);
        return new ActionResponse("Password has been reset. Please log in with your new password.");
    }

    private void sendVerificationEmail(User user) {
        String token = accountTokenService.create(user, AccountTokenType.EMAIL_VERIFICATION, 24 * 60);
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    private AuthResponse issueTokens(User user) {
        String token = jwtProvider.generateToken(user.getEmail());
        String refreshToken = refreshTokenService.create(user);

        userService.updateLastAccessDate(user.getId());

        return new AuthResponse(
                token,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getDisplayName()
        );
    }
}
