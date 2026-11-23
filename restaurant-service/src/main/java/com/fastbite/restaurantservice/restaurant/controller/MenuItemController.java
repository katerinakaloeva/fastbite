package com.fastbite.restaurantservice.restaurant.controller;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuItemRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuItemResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateMenuItemRequest;
import com.fastbite.restaurantservice.restaurant.security.CurrentUser;
import com.fastbite.restaurantservice.restaurant.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Menu items",
        description = "Operations for managing the items of a menu category."
)
@RestController
@RequestMapping("/menu-categories/{categoryId}/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Operation(
            summary = "Create a menu item",
            description = "Creates a menu item in a menu category."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Menu item created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid menu item data."),
            @ApiResponse(responseCode = "404", description = "Menu category not found.")
    })
    @PostMapping
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CreateMenuItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        MenuItemResponse response = menuItemService.createMenuItem(categoryId, request, CurrentUser.from(jwt));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update a menu item",
            description = "Updates only the menu item fields provided in the request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu item updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid menu item data."),
            @ApiResponse(responseCode = "404", description = "Menu category or menu item not found.")
    })
    @PatchMapping("/{itemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable UUID categoryId,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateMenuItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        MenuItemResponse response = menuItemService.updateMenuItem(categoryId, itemId, request, CurrentUser.from(jwt));

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get available menu items",
            description = "Returns the available menu items of a menu category."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu items returned successfully."),
            @ApiResponse(responseCode = "404", description = "Menu category not found.")
    })
    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAvailableItemsByCategoryId(
            @PathVariable UUID categoryId
    ) {
        List<MenuItemResponse> response =
                menuItemService.getAvailableItemsByCategoryId(categoryId);

        return ResponseEntity.ok(response);
    }
}
