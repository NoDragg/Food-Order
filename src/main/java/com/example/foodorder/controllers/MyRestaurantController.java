package com.example.foodorder.controllers;

import com.example.foodorder.dto.CreateRestaurantRequest;
import com.example.foodorder.dto.RestaurantMapper;
import com.example.foodorder.dto.RestaurantResponse;
import com.example.foodorder.entities.Cuisine;
import com.example.foodorder.entities.MenuItem;
import com.example.foodorder.entities.Restaurant;
import com.example.foodorder.entities.User;
import com.example.foodorder.repositories.MenuItemRepository;
import com.example.foodorder.repositories.RestaurantRepository;
import com.example.foodorder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/my/restaurant")
@RequiredArgsConstructor
@Transactional
public class MyRestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final EntityManager entityManager;

    @GetMapping
    public ResponseEntity<RestaurantResponse> getMyRestaurant(@RequestHeader("Authorization") String authorization) {
        String auth0Id = extractAuth0Id(authorization);
        return userRepository.findByAuthId(auth0Id)
                .flatMap(user -> restaurantRepository.findByUserId(user.getId()))
                .map(restaurant -> ResponseEntity.ok(RestaurantMapper.toResponse(restaurant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> createMyRestaurant(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateRestaurantRequest request) {
        String auth0Id = extractAuth0Id(authorization);

        User user = userRepository.findByAuthId(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already has a restaurant
        if (restaurantRepository.findByUserId(user.getId()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setUser(user);
        restaurant.setRestaurantName(request.getRestaurantName());
        restaurant.setCity(request.getCity());
        restaurant.setCountry(request.getCountry());
        restaurant.setDeliveryPrice(request.getDeliveryPrice());
        restaurant.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        restaurant.setImageUrl(request.getImageUrl() != null ? request.getImageUrl() : "");
        restaurant.setLastUpdated(Instant.now());
        restaurant.setCreatedAt(Instant.now());

        // Save restaurant first
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        // Save menu items
        if (request.getMenuItems() != null) {
            for (CreateRestaurantRequest.MenuItemRequest menuItemReq : request.getMenuItems()) {
                MenuItem menuItem = new MenuItem();
                menuItem.setName(menuItemReq.getName());
                menuItem.setPrice(menuItemReq.getPrice());
                menuItem.setRestaurant(savedRestaurant);
                menuItemRepository.save(menuItem);
            }
        }

        // Handle cuisines
        saveCuisines(savedRestaurant, request.getCuisines());

        // Reload to get full data
        Restaurant fullRestaurant = restaurantRepository.findById(savedRestaurant.getId()).orElse(savedRestaurant);
        return ResponseEntity.ok(RestaurantMapper.toResponse(fullRestaurant));
    }

    @PutMapping
    public ResponseEntity<RestaurantResponse> updateMyRestaurant(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateRestaurantRequest request) {
        String auth0Id = extractAuth0Id(authorization);

        User user = userRepository.findByAuthId(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurant = restaurantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setRestaurantName(request.getRestaurantName());
        restaurant.setCity(request.getCity());
        restaurant.setCountry(request.getCountry());
        restaurant.setDeliveryPrice(request.getDeliveryPrice());
        restaurant.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        if (request.getImageUrl() != null) {
            restaurant.setImageUrl(request.getImageUrl());
        }
        restaurant.setLastUpdated(Instant.now());

        // Update menu items - clear and re-add
        menuItemRepository.deleteAll(menuItemRepository.findByRestaurantId(restaurant.getId()));
        if (request.getMenuItems() != null) {
            for (CreateRestaurantRequest.MenuItemRequest menuItemReq : request.getMenuItems()) {
                MenuItem menuItem = new MenuItem();
                menuItem.setName(menuItemReq.getName());
                menuItem.setPrice(menuItemReq.getPrice());
                menuItem.setRestaurant(restaurant);
                menuItemRepository.save(menuItem);
            }
        }

        // Update cuisines
        saveCuisines(restaurant, request.getCuisines());

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        Restaurant fullRestaurant = restaurantRepository.findById(savedRestaurant.getId()).orElse(savedRestaurant);
        return ResponseEntity.ok(RestaurantMapper.toResponse(fullRestaurant));
    }

    private void saveCuisines(Restaurant restaurant, List<String> cuisineNames) {
        if (cuisineNames == null || cuisineNames.isEmpty())
            return;

        restaurant.getCuisineList().clear();
        restaurantRepository.save(restaurant);

        List<Cuisine> cuisines = new ArrayList<>();
        for (String name : cuisineNames) {
            TypedQuery<Cuisine> query = entityManager.createQuery(
                    "SELECT c FROM Cuisine c WHERE c.name = :name", Cuisine.class);
            query.setParameter("name", name);
            List<Cuisine> existing = query.getResultList();
            if (!existing.isEmpty()) {
                cuisines.add(existing.get(0));
            } else {
                Cuisine newCuisine = new Cuisine();
                newCuisine.setName(name);
                entityManager.persist(newCuisine);
                cuisines.add(newCuisine);
            }
        }
        restaurant.setCuisineList(cuisines);
    }

    private String extractAuth0Id(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
