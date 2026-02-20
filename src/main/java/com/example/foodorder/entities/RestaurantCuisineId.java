package com.example.foodorder.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class RestaurantCuisineId implements java.io.Serializable {
    private static final long serialVersionUID = 723268263039769541L;
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "cuisine_id", nullable = false)
    private Long cuisineId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RestaurantCuisineId entity = (RestaurantCuisineId) o;
        return Objects.equals(this.cuisineId, entity.cuisineId) &&
                Objects.equals(this.restaurantId, entity.restaurantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cuisineId, restaurantId);
    }

}