package com.reclaim.app.auth.dto;

import jakarta.validation.constraints.*;

public class LoginRequest {
    @NotBlank @Email
    public String email;

    @NotBlank
    public String password;
}
