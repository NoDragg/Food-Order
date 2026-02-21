package com.example.foodorder.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RestaurantResponse {
    private Long id;
    private Long userId;
    private String restaurantName;
    private String city;
    private String country;
    private BigDecimal deliveryPrice;
    private Integer estimatedDeliveryTime;
    private List<String> cuisines;
    private List<MenuItemResponse> menuItems;
    private String imageUrl;
    private String lastUpdated;
}
