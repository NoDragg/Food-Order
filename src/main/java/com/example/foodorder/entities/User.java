package com.example.foodorder.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Used for Auth0 / legacy auth (nullable now that we have username/password)
    @Column(name = "auth_id")
    private String authId;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    // ---- New columns for username/password auth ----
    @Column(name = "username", unique = true, length = 100)
    private String username;

    /** SHA-256 hex hash of the password (hashed on frontend, stored as-is) */
    @Column(name = "password_hash", length = 64)
    private String passwordHash;

    /** USER or RESTAURANT_OWNER */
    @Column(name = "role", length = 30)
    private String role = "USER";
    // -----------------------------------------------

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at")
    private Instant createdAt;
}