package com.example.foodorder.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "delivery_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryPrice;

    @Column(name = "estimated_delivery_time", nullable = false)
    private Integer estimatedDeliveryTime;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at")
    private Instant createdAt;

}