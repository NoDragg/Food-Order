package com.example.foodorder.controllers;

import com.example.foodorder.dto.RestaurantMapper;
import com.example.foodorder.dto.RestaurantResponse;
import com.example.foodorder.dto.RestaurantSearchResponse;
import com.example.foodorder.entities.Restaurant;
import com.example.foodorder.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sub-restaurants")
@RequiredArgsConstructor
public class SubRestaurantController {

    private final RestaurantRepository restaurantRepository;

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        return restaurantRepository.findById(id)
                .map(restaurant -> ResponseEntity.ok(RestaurantMapper.toResponse(restaurant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/{city}")
    public ResponseEntity<RestaurantSearchResponse> searchRestaurants(
            @PathVariable String city,
            @RequestParam(defaultValue = "") String searchQuery,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String selectedCuisines,
            @RequestParam(defaultValue = "bestMatch") String sortOption) {

        // Parse cuisines
        List<String> cuisineList = null;
        if (selectedCuisines != null && !selectedCuisines.isEmpty()) {
            cuisineList = Arrays.asList(selectedCuisines.split(","));
        }

        // Determine sort
        Sort sort = switch (sortOption) {
            case "deliveryPrice" -> Sort.by("deliveryPrice").ascending();
            case "estimatedDeliveryTime" -> Sort.by("estimatedDeliveryTime").ascending();
            case "lastUpdated" -> Sort.by("lastUpdated").descending();
            default -> Sort.by("restaurantName").ascending(); // bestMatch
        };

        // Page is 1-indexed from frontend, Spring is 0-indexed
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), 10, sort);

        Page<Restaurant> restaurantPage;
        if (cuisineList != null && !cuisineList.isEmpty()) {
            restaurantPage = restaurantRepository.searchRestaurantsWithCuisines(
                    city, searchQuery, cuisineList, pageable);
        } else {
            restaurantPage = restaurantRepository.searchRestaurants(
                    city, searchQuery, pageable);
        }

        // Build response
        RestaurantSearchResponse response = new RestaurantSearchResponse();
        response.setData(restaurantPage.getContent().stream()
                .map(RestaurantMapper::toResponse)
                .collect(Collectors.toList()));

        RestaurantSearchResponse.PaginationInfo pagination = new RestaurantSearchResponse.PaginationInfo();
        pagination.setTotal(restaurantPage.getTotalElements());
        pagination.setPage(page);
        pagination.setPages(restaurantPage.getTotalPages());
        response.setPagination(pagination);

        return ResponseEntity.ok(response);
    }

    /**
     * Unified search — searches across restaurant name, city, AND menu item names.
     * GET
     * /api/sub-restaurants/search?q=pizza&selectedCuisines=Italian&sortOption=bestMatch&page=1
     */
    @GetMapping("/search")
    public ResponseEntity<RestaurantSearchResponse> unifiedSearch(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String selectedCuisines,
            @RequestParam(defaultValue = "bestMatch") String sortOption) {

        List<String> cuisineList = null;
        if (selectedCuisines != null && !selectedCuisines.isEmpty()) {
            cuisineList = Arrays.asList(selectedCuisines.split(","));
        }

        Sort sort = switch (sortOption) {
            case "deliveryPrice" -> Sort.by("deliveryPrice").ascending();
            case "estimatedDeliveryTime" -> Sort.by("estimatedDeliveryTime").ascending();
            case "lastUpdated" -> Sort.by("lastUpdated").descending();
            default -> Sort.by("restaurantName").ascending();
        };

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), 10, sort);

        Page<Restaurant> restaurantPage;
        if (cuisineList != null && !cuisineList.isEmpty()) {
            restaurantPage = restaurantRepository.unifiedSearchWithCuisines(q, cuisineList, pageable);
        } else {
            restaurantPage = restaurantRepository.unifiedSearch(q, pageable);
        }

        RestaurantSearchResponse response = new RestaurantSearchResponse();
        response.setData(restaurantPage.getContent().stream()
                .map(RestaurantMapper::toResponse)
                .collect(Collectors.toList()));

        RestaurantSearchResponse.PaginationInfo pagination = new RestaurantSearchResponse.PaginationInfo();
        pagination.setTotal(restaurantPage.getTotalElements());
        pagination.setPage(page);
        pagination.setPages(restaurantPage.getTotalPages());
        response.setPagination(pagination);

        return ResponseEntity.ok(response);
    }
}
