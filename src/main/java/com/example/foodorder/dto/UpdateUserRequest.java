package com.example.foodorder.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String addressLine1;
    private String city;
    private String country;
}
