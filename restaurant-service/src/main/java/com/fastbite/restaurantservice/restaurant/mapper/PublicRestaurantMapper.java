package com.fastbite.restaurantservice.restaurant.mapper;

import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantDetailsResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantSummaryResponse;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class PublicRestaurantMapper {

    public PublicRestaurantSummaryResponse toResponse(Restaurant restaurant) {
        return new PublicRestaurantSummaryResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getCity(),
                restaurant.getMunicipality(),
                restaurant.getRegion()
        );
    }

    public PublicRestaurantDetailsResponse toDetailsResponse(Restaurant restaurant) {
        return new PublicRestaurantDetailsResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getAddress(),
                restaurant.getPhone(),
                restaurant.getCity(),
                restaurant.getMunicipality(),
                restaurant.getRegion(),
                restaurant.getPostalCode()
        );
    }
}
