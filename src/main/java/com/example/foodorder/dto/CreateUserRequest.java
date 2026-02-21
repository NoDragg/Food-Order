package com.example.foodorder.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String auth0Id;
    private String email;
}
