package com.rosewood.dto;

import com.rosewood.enums.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String password;
    private UserRole role;
}
