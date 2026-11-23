package com.fastbite.restaurantservice.restaurant.mapper;

import com.fastbite.restaurantservice.restaurant.dto.CreateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.dto.RestaurantResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import org.junit.jupiter.api.Test;

import static com.fastbite.restaurantservice.restaurant.security.TestUsers.OWNER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RestaurantMapperTest {

    private final RestaurantMapper restaurantMapper = new RestaurantMapper();

    @Test
    void mapsMunicipalityAndRegionFromCreateRequestToEntityAndResponse() {
        CreateRestaurantRequest request = new CreateRestaurantRequest(
                "FastBite Athens",
                "Athens, Greece",
                "Fast food restaurant",
                "+30 210 123 4567",
                "Athens",
                "Athens Municipality",
                "Attica",
                "10552"
        );

        Restaurant restaurant = restaurantMapper.toEntity(request, OWNER_ID);
        RestaurantResponse response = restaurantMapper.toResponse(restaurant);

        assertEquals("Athens Municipality", restaurant.getMunicipality());
        assertEquals("Attica", restaurant.getRegion());
        assertEquals("Athens Municipality", response.municipality());
        assertEquals("Attica", response.region());
    }

    @Test
    void updatesMunicipalityAndRegionWhenProvided() {
        Restaurant restaurant = Restaurant.builder()
                .name("FastBite Athens")
                .address("Athens, Greece")
                .municipality("Old Municipality")
                .region("Old Region")
                .build();
        UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                null,
                null,
                null,
                null,
                null,
                "Athens Municipality",
                "Attica",
                null,
                null
        );

        restaurantMapper.updateEntity(request, restaurant);

        assertEquals("Athens Municipality", restaurant.getMunicipality());
        assertEquals("Attica", restaurant.getRegion());
    }
}
