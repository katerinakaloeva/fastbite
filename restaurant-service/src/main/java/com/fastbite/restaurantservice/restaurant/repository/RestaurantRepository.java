package com.fastbite.restaurantservice.restaurant.repository;

import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
}