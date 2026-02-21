package com.example.foodorder.dto;

import com.example.foodorder.entities.Cuisine;
import com.example.foodorder.entities.MenuItem;
import com.example.foodorder.entities.Restaurant;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantMapper {

    public static RestaurantResponse toResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurant.getId());
        response.setUserId(restaurant.getUser() != null ? restaurant.getUser().getId() : null);
        response.setRestaurantName(restaurant.getRestaurantName());
        response.setCity(restaurant.getCity());
        response.setCountry(restaurant.getCountry());
        response.setDeliveryPrice(restaurant.getDeliveryPrice());
        response.setEstimatedDeliveryTime(restaurant.getEstimatedDeliveryTime());
        response.setImageUrl(restaurant.getImageUrl());
        response.setLastUpdated(restaurant.getLastUpdated() != null ? restaurant.getLastUpdated().toString() : null);

        // Map cuisines to list of cuisine names
        if (restaurant.getCuisineList() != null) {
            response.setCuisines(restaurant.getCuisineList().stream()
                    .map(Cuisine::getName)
                    .collect(Collectors.toList()));
        }

        // Map menu items
        if (restaurant.getMenuItems() != null) {
            response.setMenuItems(restaurant.getMenuItems().stream()
                    .map(RestaurantMapper::toMenuItemResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    public static MenuItemResponse toMenuItemResponse(MenuItem menuItem) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(menuItem.getId());
        response.setName(menuItem.getName());
        response.setPrice(menuItem.getPrice());
        return response;
    }

    public static List<RestaurantResponse> toResponseList(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantMapper::toResponse)
                .collect(Collectors.toList());
    }
}
