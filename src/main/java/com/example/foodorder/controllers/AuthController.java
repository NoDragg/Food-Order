package com.example.foodorder.controllers;

import com.example.foodorder.dto.LoginRequest;
import com.example.foodorder.dto.LoginResponse;
import com.example.foodorder.dto.RegisterRequest;
import com.example.foodorder.entities.User;
import com.example.foodorder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    // ──────────────────────────────────────────────
    // POST /api/auth/register
    // ──────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Validate fields
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username không được trống."));
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password không được trống."));
        }

        // Check username taken
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Tên đăng nhập đã tồn tại."));
        }

        // Determine role
        String role = "USER";
        if ("RESTAURANT_OWNER".equalsIgnoreCase(request.getRole())) {
            role = "RESTAURANT_OWNER";
        }

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword()); // frontend already sent SHA-256 hex
        user.setEmail(request.getEmail() != null ? request.getEmail() : "");
        user.setName(request.getName() != null ? request.getName() : request.getUsername());
        user.setRole(role);
        user.setAuthId(UUID.randomUUID().toString()); // keep non-null for legacy compat
        user.setCreatedAt(Instant.now());

        User saved = userRepository.save(user);

        // Return token (simple: use "userId:role" as token for now — frontend stores
        // it)
        String token = buildToken(saved);
        return ResponseEntity.ok(new LoginResponse(token, saved.getId(), saved.getRole(), saved.getUsername()));
    }

    // ──────────────────────────────────────────────
    // POST /api/auth/login
    // ──────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username và password là bắt buộc."));
        }

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Tài khoản không tồn tại."));
        }

        User user = userOpt.get();

        // Compare SHA-256 hash sent by frontend with stored hash
        if (!request.getPassword().equals(user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Mật khẩu không đúng."));
        }

        String token = buildToken(user);
        return ResponseEntity.ok(new LoginResponse(token, user.getId(), user.getRole(), user.getUsername()));
    }

    /**
     * Builds a simple JWT-style token: base64(header).base64(payload).signature
     * This is a DEMO token — replace with proper JWT (JJWT library) for production.
     */
    private String buildToken(User user) {
        String header = java.util.Base64.getUrlEncoder().encodeToString(
                "{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes());
        String payload = java.util.Base64.getUrlEncoder().encodeToString(
                String.format("{\"sub\":\"%d\",\"username\":\"%s\",\"role\":\"%s\",\"iat\":%d}",
                        user.getId(), user.getUsername(), user.getRole(),
                        Instant.now().getEpochSecond()).getBytes());
        return header + "." + payload + ".signed";
    }
}
