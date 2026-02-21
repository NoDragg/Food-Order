package com.example.foodorder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;  // SHA-256 hex from frontend
}
