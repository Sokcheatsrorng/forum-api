package com.forum.dto.users;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Builder
public record ChangePasswordRequest(

        @NotBlank(message = "Current password is required") String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") String newPassword,

        @NotBlank(message = "Password confirmation is required") String confirmedNewPassword
) {
}
