package com.forum.dto.users;

import lombok.Builder;

@Builder
public record ChangePasswordRequest(

        String oldPassword,

        String newPassword,

        String confirmedNewPassword
) {
}
