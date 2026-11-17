package com.fastbite.restaurantservice.restaurant.controller;

import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantDetailsResponse;
import com.fastbite.restaurantservice.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Public Restaurants",
        description = "Public restaurant information for customers."
)
@RestController
@RequestMapping("/public/restaurants")
@RequiredArgsConstructor
public class PublicRestaurantController {

    private final RestaurantService restaurantService;

    @Operation(
            summary = "Get public restaurant details",
            description = "Returns public details for one active restaurant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant details returned successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid restaurant ID format."),
            @ApiResponse(responseCode = "404", description = "Restaurant not found or inactive.")
    })
    @GetMapping("/{restaurantId}")
    public ResponseEntity<PublicRestaurantDetailsResponse> getPublicRestaurantDetails(
            @PathVariable UUID restaurantId
    ) {
        PublicRestaurantDetailsResponse response = restaurantService
                .getPublicRestaurantDetails(restaurantId);

        return ResponseEntity.ok(response);
    }
}
