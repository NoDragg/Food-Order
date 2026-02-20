package com.example.foodorder.services;

import com.example.foodorder.entities.Restaurant;
import java.util.List;

public interface RestaurantService {
    List<Restaurant> getAllRestaurants();

    Restaurant getRestaurantById(Long id);

    Restaurant createRestaurant(Restaurant restaurant);

    Restaurant updateRestaurant(Long id, Restaurant restaurantDetails);

    void deleteRestaurant(Long id);
}
