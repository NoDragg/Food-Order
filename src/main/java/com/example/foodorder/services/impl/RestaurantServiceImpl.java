package com.example.foodorder.services.impl;

import com.example.foodorder.entities.Restaurant;
import com.example.foodorder.repositories.RestaurantRepository;
import com.example.foodorder.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant updateRestaurant(Long id, Restaurant restaurantDetails) {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setRestaurantName(restaurantDetails.getRestaurantName());
        restaurant.setCity(restaurantDetails.getCity());
        restaurant.setCountry(restaurantDetails.getCountry());
        restaurant.setDeliveryPrice(restaurantDetails.getDeliveryPrice());
        restaurant.setEstimatedDeliveryTime(restaurantDetails.getEstimatedDeliveryTime());
        restaurant.setImageUrl(restaurantDetails.getImageUrl());

        return restaurantRepository.save(restaurant);
    }

    @Override
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurantRepository.delete(restaurant);
    }
}
