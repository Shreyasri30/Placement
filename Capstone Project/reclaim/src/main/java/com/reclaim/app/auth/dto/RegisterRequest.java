package com.reclaim.app.auth.dto;

import jakarta.validation.constraints.*;

public class RegisterRequest {
    @NotBlank @Size(min=2, max=100)
    public String name;

    @NotBlank @Email
    public String email;

    @NotBlank @Size(min=6, max=30)
    public String password;
}
