package com.fastbite.restaurantservice.restaurant.controller;

import com.fastbite.restaurantservice.restaurant.dto.CreateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.dto.RestaurantResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Restaurants",
        description = "Operations for managing restaurants."
)
@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(
            summary = "Create a restaurant",
            description = "Creates a new restaurant and returns its details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurant created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid restaurant data.")
    })
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request
    ) {
        RestaurantResponse response = restaurantService.createRestaurant(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update a restaurant",
            description = "Updates only the restaurant fields provided in the request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid restaurant data."),
            @ApiResponse(responseCode = "404", description = "Restaurant not found.")
    })
    @PatchMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody UpdateRestaurantRequest request
    ) {
        RestaurantResponse response = restaurantService.updateRestaurant(
                restaurantId,
                request
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all restaurants",
            description = "Returns all restaurants."
    )
    @ApiResponse(responseCode = "200", description = "Restaurants returned successfully.")
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        List<RestaurantResponse> response = restaurantService.getAllRestaurants();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a restaurant by ID",
            description = "Returns the details of one restaurant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant returned successfully."),
            @ApiResponse(responseCode = "404", description = "Restaurant not found.")
    })
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @PathVariable UUID restaurantId
    ) {
        RestaurantResponse response = restaurantService.getRestaurantById(restaurantId);

        return ResponseEntity.ok(response);
    }
}
