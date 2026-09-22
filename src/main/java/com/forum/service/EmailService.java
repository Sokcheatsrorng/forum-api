package com.forum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:true}")
    private boolean enabled;

    @Value("${app.mail.from:sokcheatsrorng@gmail.com}")
    private String from;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public void sendVerificationEmail(String email, String token) {
        send(email, "Verify your Forum account",
                "Welcome to Forum. Verify your email by opening: "
                        + frontendUrl + "/verify-email?token=" + token);
    }

    public void sendPasswordResetEmail(String email, String token) {
        send(email, "Reset your Forum password",
                "We received a password reset request. Reset your password here: "
                        + frontendUrl + "/reset-password?token=" + token
                        + "\n\nThis link expires in 30 minutes. If you did not request it, you can ignore this email.");
    }

    private void send(String recipient, String subject, String text) {
        if (!enabled) {
            log.warn("Email is disabled; no email sent to {}. Set APP_MAIL_ENABLED=true and SMTP settings to enable delivery.", recipient);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sokcheatsrorng@gmail.com");
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
