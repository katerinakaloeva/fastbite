package com.fastbite.restaurantservice.restaurant.controller;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuItemRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuItemResponse;
import com.fastbite.restaurantservice.restaurant.exception.MenuCategoryNotFoundException;
import com.fastbite.restaurantservice.restaurant.service.MenuItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuItemController.class)
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuItemService menuItemService;

    @Test
    void createsMenuItemWhenRequestIsValid() throws Exception {
        UUID categoryId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        MenuItemResponse response = menuItemResponse(
                itemId,
                categoryId,
                "Cheeseburger",
                new BigDecimal("8.50")
        );

        when(menuItemService.createMenuItem(
                eq(categoryId),
                any(CreateMenuItemRequest.class)
        )).thenReturn(response);

        mockMvc.perform(post("/menu-categories/{categoryId}/menu-items", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Cheeseburger",
                                  "description": "Beef burger with cheese",
                                  "price": 8.50,
                                  "currency": "EUR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemId.toString()))
                .andExpect(jsonPath("$.categoryId").value(categoryId.toString()))
                .andExpect(jsonPath("$.name").value("Cheeseburger"))
                .andExpect(jsonPath("$.price").value(8.50))
                .andExpect(jsonPath("$.currency").value("EUR"));

        verify(menuItemService).createMenuItem(
                categoryId,
                new CreateMenuItemRequest(
                        "Cheeseburger",
                        "Beef burger with cheese",
                        new BigDecimal("8.50"),
                        "EUR"
                )
        );
    }

    @Test
    void returnsAvailableItemsForCategory() throws Exception {
        UUID categoryId = UUID.randomUUID();
        MenuItemResponse cheeseburger = menuItemResponse(
                UUID.randomUUID(),
                categoryId,
                "Cheeseburger",
                new BigDecimal("8.50")
        );
        MenuItemResponse chickenBurger = menuItemResponse(
                UUID.randomUUID(),
                categoryId,
                "Chicken Burger",
                new BigDecimal("7.50")
        );

        when(menuItemService.getAvailableItemsByCategoryId(categoryId))
                .thenReturn(List.of(cheeseburger, chickenBurger));

        mockMvc.perform(get("/menu-categories/{categoryId}/menu-items", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cheeseburger"))
                .andExpect(jsonPath("$[0].price").value(8.50))
                .andExpect(jsonPath("$[1].name").value("Chicken Burger"))
                .andExpect(jsonPath("$[1].price").value(7.50));

        verify(menuItemService).getAvailableItemsByCategoryId(categoryId);
    }

    @Test
    void returnsValidationErrorWhenRequestIsInvalid() throws Exception {
        UUID categoryId = UUID.randomUUID();

        mockMvc.perform(post("/menu-categories/{categoryId}/menu-items", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "price": -1,
                                  "currency": "eur"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.name").value("Menu item name is required"))
                .andExpect(jsonPath("$.fieldErrors.price").value("Price must be zero or greater"))
                .andExpect(jsonPath("$.fieldErrors.currency")
                        .value("Currency must be a 3-letter uppercase code"));

        verifyNoInteractions(menuItemService);
    }

    @Test
    void returnsNotFoundErrorWhenCategoryDoesNotExist() throws Exception {
        UUID categoryId = UUID.randomUUID();

        when(menuItemService.getAvailableItemsByCategoryId(categoryId))
                .thenThrow(new MenuCategoryNotFoundException(categoryId));

        mockMvc.perform(get("/menu-categories/{categoryId}/menu-items", categoryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MENU_CATEGORY_NOT_FOUND"));

        verify(menuItemService).getAvailableItemsByCategoryId(categoryId);
    }

    private MenuItemResponse menuItemResponse(
            UUID itemId,
            UUID categoryId,
            String name,
            BigDecimal price
    ) {
        return new MenuItemResponse(
                itemId,
                categoryId,
                name,
                null,
                price,
                "EUR",
                true,
                null,
                null
        );
    }
}
