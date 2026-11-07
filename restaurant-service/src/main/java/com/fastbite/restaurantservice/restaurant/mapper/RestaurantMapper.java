package com.fastbite.restaurantservice.restaurant.mapper;

import com.fastbite.restaurantservice.restaurant.dto.CreateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.dto.RestaurantResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public Restaurant toEntity(CreateRestaurantRequest request) {
        return Restaurant.builder()
                .name(request.name())
                .address(request.address())
                .description(request.description())
                .phone(request.phone())
                .city(request.city())
                .postalCode(request.postalCode())
                .build();
    }

    public void updateEntity(
            UpdateRestaurantRequest request,
            Restaurant restaurant
    ) {
        restaurant.updateDetails(
                request.name(),
                request.address(),
                request.description(),
                request.phone(),
                request.city(),
                request.postalCode(),
                request.active()
        );
    }

    public RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.isActive(),
                restaurant.getDescription(),
                restaurant.getPhone(),
                restaurant.getCity(),
                restaurant.getPostalCode(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
        );
    }
}
