package com.fastbite.restaurantservice.restaurant.controller;

import com.fastbite.restaurantservice.restaurant.dto.CreateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.dto.PublicMenuResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantPageResponse;
import com.fastbite.restaurantservice.restaurant.dto.RestaurantResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.security.CurrentUser;
import com.fastbite.restaurantservice.restaurant.service.PublicMenuService;
import com.fastbite.restaurantservice.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final PublicMenuService publicMenuService;

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
            @Valid @RequestBody CreateRestaurantRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        RestaurantResponse response = restaurantService.createRestaurant(request, ownerId);

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
            @Valid @RequestBody UpdateRestaurantRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        RestaurantResponse response = restaurantService.updateRestaurant(
                restaurantId,
                request,
                CurrentUser.from(jwt)
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search active restaurants",
            description = "Returns active restaurants with optional location filters and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant page returned successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters.")
    })
    @GetMapping
    public ResponseEntity<PublicRestaurantPageResponse> searchPublicRestaurants(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PublicRestaurantPageResponse response = restaurantService.searchPublicRestaurants(
                city,
                municipality,
                region,
                pageable
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a restaurant's public menu",
            description = "Returns active categories and available menu items for an active restaurant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Public menu returned successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid restaurant ID format."),
            @ApiResponse(responseCode = "404", description = "Restaurant not found or inactive.")
    })
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<PublicMenuResponse> getPublicMenu(
            @PathVariable UUID restaurantId
    ) {
        PublicMenuResponse response = publicMenuService.getPublicMenu(restaurantId);

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
