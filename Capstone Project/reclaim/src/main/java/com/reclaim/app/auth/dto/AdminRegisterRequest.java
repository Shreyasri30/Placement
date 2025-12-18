package com.reclaim.app.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AdminRegisterRequest {
    @NotBlank
    @Email
    public String email;

    @NotBlank
    public String name;

    @NotBlank
    public String password;

    @NotBlank
    public String securityCode;
}
