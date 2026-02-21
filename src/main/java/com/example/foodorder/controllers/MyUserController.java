package com.example.foodorder.controllers;

import com.example.foodorder.dto.CreateUserRequest;
import com.example.foodorder.dto.UpdateUserRequest;
import com.example.foodorder.entities.User;
import com.example.foodorder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/my/user")
@RequiredArgsConstructor
public class MyUserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        String auth0Id = extractAuth0Id(authorization);
        return userRepository.findByAuthId(auth0Id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createCurrentUser(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateUserRequest request) {
        String auth0Id = extractAuth0Id(authorization);

        // Check if user already exists
        if (userRepository.findByAuthId(auth0Id).isPresent()) {
            return ResponseEntity.ok(userRepository.findByAuthId(auth0Id).get());
        }

        User user = new User();
        user.setAuthId(auth0Id);
        user.setEmail(request.getEmail());
        user.setCreatedAt(Instant.now());

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping
    public ResponseEntity<User> updateCurrentUser(
            @RequestHeader("Authorization") String authorization,
            @RequestBody UpdateUserRequest request) {
        String auth0Id = extractAuth0Id(authorization);

        return userRepository.findByAuthId(auth0Id)
                .map(user -> {
                    user.setName(request.getName());
                    user.setAddressLine1(request.getAddressLine1());
                    user.setCity(request.getCity());
                    user.setCountry(request.getCountry());
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Extract auth0 user id from "Bearer <token>" header
    // Since we're not validating JWT, we just use the token value as the user
    // identifier
    private String extractAuth0Id(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
