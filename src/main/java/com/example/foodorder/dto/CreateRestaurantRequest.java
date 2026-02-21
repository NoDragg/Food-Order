package com.example.foodorder.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateRestaurantRequest {
    private String restaurantName;
    private String city;
    private String country;
    private BigDecimal deliveryPrice;
    private Integer estimatedDeliveryTime;
    private List<String> cuisines;
    private List<MenuItemRequest> menuItems;
    private String imageUrl;

    @Data
    public static class MenuItemRequest {
        private String name;
        private BigDecimal price;
    }
}
