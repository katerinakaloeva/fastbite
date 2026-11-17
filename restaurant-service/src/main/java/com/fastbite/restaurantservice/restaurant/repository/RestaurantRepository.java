package com.fastbite.restaurantservice.restaurant.repository;

import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    @Query("""
            select restaurant
            from Restaurant restaurant
            where restaurant.active = true
              and (:city is null or lower(restaurant.city) = lower(:city))
              and (:municipality is null or lower(restaurant.municipality) = lower(:municipality))
              and (:region is null or lower(restaurant.region) = lower(:region))
            """)
    Page<Restaurant> searchActiveRestaurants(
            @Param("city") String city,
            @Param("municipality") String municipality,
            @Param("region") String region,
            Pageable pageable
    );
}