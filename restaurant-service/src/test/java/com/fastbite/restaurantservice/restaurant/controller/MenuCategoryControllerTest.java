package com.fastbite.restaurantservice.restaurant.controller;

import com.fastbite.restaurantservice.restaurant.dto.CreateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.dto.MenuCategoryResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateMenuCategoryRequest;
import com.fastbite.restaurantservice.restaurant.exception.MenuCategoryNotFoundException;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantNotFoundException;
import com.fastbite.restaurantservice.restaurant.service.MenuCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuCategoryController.class)
class MenuCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuCategoryService menuCategoryService;

    @Test
    void createsMenuCategoryWhenRequestIsValid() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        MenuCategoryResponse response = categoryResponse(categoryId, restaurantId, "Burgers", 0);

        when(menuCategoryService.createMenuCategory(
                eq(restaurantId),
                any(CreateMenuCategoryRequest.class)
        )).thenReturn(response);

        mockMvc.perform(post("/restaurants/{restaurantId}/menu-categories", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Burgers",
                                  "displayOrder": 0
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.restaurantId").value(restaurantId.toString()))
                .andExpect(jsonPath("$.name").value("Burgers"))
                .andExpect(jsonPath("$.displayOrder").value(0));

        verify(menuCategoryService).createMenuCategory(
                restaurantId,
                new CreateMenuCategoryRequest("Burgers", 0)
        );
    }

    @Test
    void updatesMenuCategoryWhenRequestIsValid() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UpdateMenuCategoryRequest request = new UpdateMenuCategoryRequest(
                "Main dishes",
                2,
                false
        );
        MenuCategoryResponse response = new MenuCategoryResponse(
                categoryId,
                restaurantId,
                "Main dishes",
                2,
                false,
                null,
                null
        );

        when(menuCategoryService.updateMenuCategory(
                eq(restaurantId),
                eq(categoryId),
                any(UpdateMenuCategoryRequest.class)
        )).thenReturn(response);

        mockMvc.perform(patch(
                        "/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Main dishes",
                                  "displayOrder": 2,
                                  "active": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.name").value("Main dishes"))
                .andExpect(jsonPath("$.displayOrder").value(2))
                .andExpect(jsonPath("$.active").value(false));

        verify(menuCategoryService).updateMenuCategory(restaurantId, categoryId, request);
    }

    @Test
    void returnsCategoriesForRestaurant() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        MenuCategoryResponse burgers = categoryResponse(
                UUID.randomUUID(),
                restaurantId,
                "Burgers",
                0
        );
        MenuCategoryResponse salads = categoryResponse(
                UUID.randomUUID(),
                restaurantId,
                "Salads",
                1
        );

        when(menuCategoryService.getCategoriesByRestaurantId(restaurantId))
                .thenReturn(List.of(burgers, salads));

        mockMvc.perform(get("/restaurants/{restaurantId}/menu-categories", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Burgers"))
                .andExpect(jsonPath("$[0].displayOrder").value(0))
                .andExpect(jsonPath("$[1].name").value("Salads"))
                .andExpect(jsonPath("$[1].displayOrder").value(1));

        verify(menuCategoryService).getCategoriesByRestaurantId(restaurantId);
    }

    @Test
    void returnsValidationErrorWhenRequestIsInvalid() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        mockMvc.perform(post("/restaurants/{restaurantId}/menu-categories", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "displayOrder": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.name").value("Menu category name is required"))
                .andExpect(jsonPath("$.fieldErrors.displayOrder")
                        .value("Display order must be zero or greater"));

        verifyNoInteractions(menuCategoryService);
    }

    @Test
    void returnsValidationErrorWhenUpdateRequestIsInvalid() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        mockMvc.perform(patch(
                        "/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayOrder": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.displayOrder").exists());

        verifyNoInteractions(menuCategoryService);
    }

    @Test
    void returnsNotFoundErrorWhenRestaurantDoesNotExist() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        when(menuCategoryService.getCategoriesByRestaurantId(restaurantId))
                .thenThrow(new RestaurantNotFoundException(restaurantId));

        mockMvc.perform(get("/restaurants/{restaurantId}/menu-categories", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESTAURANT_NOT_FOUND"));

        verify(menuCategoryService).getCategoriesByRestaurantId(restaurantId);
    }

    @Test
    void returnsNotFoundErrorWhenMenuCategoryDoesNotExist() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UpdateMenuCategoryRequest request = new UpdateMenuCategoryRequest(
                null,
                2,
                null
        );

        when(menuCategoryService.updateMenuCategory(
                eq(restaurantId),
                eq(categoryId),
                any(UpdateMenuCategoryRequest.class)
        )).thenThrow(new MenuCategoryNotFoundException(categoryId));

        mockMvc.perform(patch(
                        "/restaurants/{restaurantId}/menu-categories/{categoryId}",
                        restaurantId,
                        categoryId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "displayOrder": 2
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MENU_CATEGORY_NOT_FOUND"));

        verify(menuCategoryService).updateMenuCategory(restaurantId, categoryId, request);
    }

    private MenuCategoryResponse categoryResponse(
            UUID categoryId,
            UUID restaurantId,
            String name,
            int displayOrder
    ) {
        return new MenuCategoryResponse(
                categoryId,
                restaurantId,
                name,
                displayOrder,
                true,
                null,
                null
        );
    }
}
