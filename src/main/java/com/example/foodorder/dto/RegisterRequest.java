package com.example.foodorder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;   // SHA-256 hex from frontend
    private String email;
    private String name;
    private String role;       // "USER" or "RESTAURANT_OWNER"
}
