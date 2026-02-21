package com.example.foodorder.repositories;

import com.example.foodorder.entities.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
        Optional<Restaurant> findByUserId(Long userId);

        List<Restaurant> findByUser_Id(Long userId);

        List<Restaurant> findByCityContainingIgnoreCase(String city);

        // ── City-based search (legacy) ──
        @Query("SELECT DISTINCT r FROM Restaurant r " +
                        "LEFT JOIN r.menuItems m " +
                        "WHERE LOWER(r.city) LIKE LOWER(CONCAT('%', :city, '%')) " +
                        "AND (:searchQuery IS NULL OR :searchQuery = '' " +
                        "     OR LOWER(r.restaurantName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
                        "     OR LOWER(m.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
        Page<Restaurant> searchRestaurants(
                        @Param("city") String city,
                        @Param("searchQuery") String searchQuery,
                        Pageable pageable);

        @Query("SELECT DISTINCT r FROM Restaurant r " +
                        "LEFT JOIN r.cuisineList c " +
                        "LEFT JOIN r.menuItems m " +
                        "WHERE LOWER(r.city) LIKE LOWER(CONCAT('%', :city, '%')) " +
                        "AND (:searchQuery IS NULL OR :searchQuery = '' " +
                        "     OR LOWER(r.restaurantName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
                        "     OR LOWER(m.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
                        "AND c.name IN :cuisines")
        Page<Restaurant> searchRestaurantsWithCuisines(
                        @Param("city") String city,
                        @Param("searchQuery") String searchQuery,
                        @Param("cuisines") List<String> cuisines,
                        Pageable pageable);

        // ── Unified search (city optional — searches restaurant name, city, AND menu
        // item name) ──
        @Query("SELECT DISTINCT r FROM Restaurant r " +
                        "LEFT JOIN r.menuItems m " +
                        "WHERE (:q IS NULL OR :q = '' " +
                        "     OR LOWER(r.restaurantName) LIKE LOWER(CONCAT('%', :q, '%')) " +
                        "     OR LOWER(r.city) LIKE LOWER(CONCAT('%', :q, '%')) " +
                        "     OR LOWER(m.name) LIKE LOWER(CONCAT('%', :q, '%')))")
        Page<Restaurant> unifiedSearch(
                        @Param("q") String q,
                        Pageable pageable);

        @Query("SELECT DISTINCT r FROM Restaurant r " +
                        "LEFT JOIN r.cuisineList c " +
                        "LEFT JOIN r.menuItems m " +
                        "WHERE (:q IS NULL OR :q = '' " +
                        "     OR LOWER(r.restaurantName) LIKE LOWER(CONCAT('%', :q, '%')) " +
                        "     OR LOWER(r.city) LIKE LOWER(CONCAT('%', :q, '%')) " +
                        "     OR LOWER(m.name) LIKE LOWER(CONCAT('%', :q, '%'))) " +
                        "AND c.name IN :cuisines")
        Page<Restaurant> unifiedSearchWithCuisines(
                        @Param("q") String q,
                        @Param("cuisines") List<String> cuisines,
                        Pageable pageable);
}
