package com.forum.dto;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    String displayName;

    String email;

    String password;
}
