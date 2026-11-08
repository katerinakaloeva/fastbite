package com.fastbite.restaurantservice.restaurant.controller;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuCategoryResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.service.MenuCategoryService;
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
        name = "Menu categories",
        description = "Operations for managing a restaurant's menu categories."
)
@RestController
@RequestMapping("/restaurants/{restaurantId}/menu-categories")
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @Operation(
            summary = "Create a menu category",
            description = "Creates a menu category for a restaurant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Menu category created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid menu category data."),
            @ApiResponse(responseCode = "404", description = "Restaurant not found.")
    })
    @PostMapping
    public ResponseEntity<MenuCategoryResponse> createMenuCategory(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateMenuCategoryRequest request
    ) {
        MenuCategoryResponse response = menuCategoryService.createMenuCategory(
                restaurantId,
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update a menu category",
            description = "Updates only the menu category fields provided in the request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu category updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid menu category data."),
            @ApiResponse(responseCode = "404", description = "Restaurant or menu category not found.")
    })
    @PatchMapping("/{categoryId}")
    public ResponseEntity<MenuCategoryResponse> updateMenuCategory(
            @PathVariable UUID restaurantId,
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateMenuCategoryRequest request
    ) {
        MenuCategoryResponse response = menuCategoryService.updateMenuCategory(
                restaurantId,
                categoryId,
                request
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a restaurant's menu categories",
            description = "Returns the menu categories of a restaurant ordered by display order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu categories returned successfully."),
            @ApiResponse(responseCode = "404", description = "Restaurant not found.")
    })
    @GetMapping
    public ResponseEntity<List<MenuCategoryResponse>> getCategoriesByRestaurantId(
            @PathVariable UUID restaurantId
    ) {
        List<MenuCategoryResponse> response =
                menuCategoryService.getCategoriesByRestaurantId(restaurantId);

        return ResponseEntity.ok(response);
    }
}
