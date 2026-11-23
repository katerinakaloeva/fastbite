package com.fastbite.restaurantservice.restaurant.mapper;

import com.fastbite.restaurantservice.restaurant.dto.CreateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.dto.RestaurantResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantMapper {

    public Restaurant toEntity(CreateRestaurantRequest request, UUID ownerId) {
        return Restaurant.builder()
                .name(request.name())
                .address(request.address())
                .description(request.description())
                .phone(request.phone())
                .city(request.city())
                .municipality(request.municipality())
                .region(request.region())
                .postalCode(request.postalCode())
                .ownerId(ownerId)
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
                request.municipality(),
                request.region(),
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
                restaurant.getMunicipality(),
                restaurant.getRegion(),
                restaurant.getPostalCode(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
        );
    }
}
