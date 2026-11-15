package com.fastbite.restaurantservice.restaurant.controller;

import com.fastbite.restaurantservice.restaurant.dto.CreateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.dto.PublicMenuCategoryResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicMenuItemResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicMenuResponse;
import com.fastbite.restaurantservice.restaurant.dto.RestaurantResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantNotFoundException;
import com.fastbite.restaurantservice.restaurant.service.PublicMenuService;
import com.fastbite.restaurantservice.restaurant.service.RestaurantService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private PublicMenuService publicMenuService;

    @Test
    void createsRestaurantWhenRequestIsValid() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        RestaurantResponse response = restaurantResponse(
                restaurantId,
                "FastBite Athens",
                "Athens, Greece"
        );

        when(restaurantService.createRestaurant(any(CreateRestaurantRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "FastBite Athens",
                                  "address": "Athens, Greece",
                                  "description": "Fast food restaurant",
                                  "phone": "+30 210 123 4567",
                                  "city": "Athens",
                                  "postalCode": "10552"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                .andExpect(jsonPath("$.name").value("FastBite Athens"))
                .andExpect(jsonPath("$.address").value("Athens, Greece"));

        verify(restaurantService).createRestaurant(new CreateRestaurantRequest(
                "FastBite Athens",
                "Athens, Greece",
                "Fast food restaurant",
                "+30 210 123 4567",
                "Athens",
                "10552"
        ));
    }

    @Test
    void updatesRestaurantWhenRequestIsValid() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                null,
                null,
                null,
                "+30 210 987 6543",
                "Piraeus",
                null,
                null
        );
        RestaurantResponse response = new RestaurantResponse(
                restaurantId,
                "FastBite Athens",
                "Athens, Greece",
                true,
                null,
                "+30 210 987 6543",
                "Piraeus",
                null,
                null,
                null
        );

        when(restaurantService.updateRestaurant(eq(restaurantId), any(UpdateRestaurantRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "+30 210 987 6543",
                                  "city": "Piraeus"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                .andExpect(jsonPath("$.phone").value("+30 210 987 6543"))
                .andExpect(jsonPath("$.city").value("Piraeus"));

        verify(restaurantService).updateRestaurant(restaurantId, request);
    }

    @Test
    void returnsAllRestaurants() throws Exception {
        RestaurantResponse firstRestaurant = restaurantResponse(
                UUID.randomUUID(),
                "FastBite Athens",
                "Athens, Greece"
        );
        RestaurantResponse secondRestaurant = restaurantResponse(
                UUID.randomUUID(),
                "FastBite Thessaloniki",
                "Thessaloniki, Greece"
        );

        when(restaurantService.getAllRestaurants())
                .thenReturn(List.of(firstRestaurant, secondRestaurant));

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("FastBite Athens"))
                .andExpect(jsonPath("$[1].name").value("FastBite Thessaloniki"));

        verify(restaurantService).getAllRestaurants();
    }

    @Test
    void returnsRestaurantByIdWhenItExists() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        RestaurantResponse response = restaurantResponse(
                restaurantId,
                "FastBite Athens",
                "Athens, Greece"
        );

        when(restaurantService.getRestaurantById(restaurantId)).thenReturn(response);

        mockMvc.perform(get("/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                .andExpect(jsonPath("$.name").value("FastBite Athens"));

        verify(restaurantService).getRestaurantById(restaurantId);
    }

    @Test
    void returnsPublicMenuWithOnlyPublicFields() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        PublicMenuResponse response = new PublicMenuResponse(
                restaurantId,
                "FastBite Athens",
                List.of(new PublicMenuCategoryResponse(
                        categoryId,
                        "Burgers",
                        1,
                        List.of(new PublicMenuItemResponse(
                                itemId,
                                "Classic Burger",
                                "Beef burger",
                                new BigDecimal("8.50"),
                                "EUR"
                        ))
                ))
        );

        when(publicMenuService.getPublicMenu(restaurantId)).thenReturn(response);

        mockMvc.perform(get("/restaurants/{restaurantId}/menu", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").value(restaurantId.toString()))
                .andExpect(jsonPath("$.restaurantName").value("FastBite Athens"))
                .andExpect(jsonPath("$.categories[0].id").value(categoryId.toString()))
                .andExpect(jsonPath("$.categories[0].name").value("Burgers"))
                .andExpect(jsonPath("$.categories[0].displayOrder").value(1))
                .andExpect(jsonPath("$.categories[0].items[0].id").value(itemId.toString()))
                .andExpect(jsonPath("$.categories[0].items[0].name").value("Classic Burger"))
                .andExpect(jsonPath("$.categories[0].items[0].price").value(8.50))
                .andExpect(jsonPath("$.categories[0].items[0].currency").value("EUR"))
                .andExpect(jsonPath("$.categories[0].active").doesNotExist())
                .andExpect(jsonPath("$.categories[0].createdAt").doesNotExist())
                .andExpect(jsonPath("$.categories[0].items[0].available").doesNotExist())
                .andExpect(jsonPath("$.categories[0].items[0].createdAt").doesNotExist());

        verify(publicMenuService).getPublicMenu(restaurantId);
    }

    @Test
    void returnsNotFoundWhenPublicMenuRestaurantDoesNotExistOrIsInactive() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        when(publicMenuService.getPublicMenu(restaurantId))
                .thenThrow(new RestaurantNotFoundException(restaurantId));

        mockMvc.perform(get("/restaurants/{restaurantId}/menu", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESTAURANT_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Restaurant with id " + restaurantId + " was not found"));

        verify(publicMenuService).getPublicMenu(restaurantId);
    }

    @Test
    void returnsValidationErrorWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "address": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.name").value("Restaurant name is required"))
                .andExpect(jsonPath("$.fieldErrors.address").value("Restaurant address is required"));

        verifyNoInteractions(restaurantService);
    }

    @Test
    void returnsValidationErrorWhenUpdateRequestIsInvalid() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        mockMvc.perform(patch("/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "1234567890123456789012345678901"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.phone").exists());

        verifyNoInteractions(restaurantService);
    }

    @Test
    void rejectsWhitespaceOnlyRestaurantNameAndAddressInPatch() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        mockMvc.perform(patch("/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "   ",
                                  "address": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name")
                        .value("Restaurant name must not be blank"))
                .andExpect(jsonPath("$.fieldErrors.address")
                        .value("Restaurant address must not be blank"));

        verifyNoInteractions(restaurantService);
    }

    @Test
    void returnsBadRequestWhenRestaurantIdIsNotAUuid() throws Exception {
        mockMvc.perform(get("/restaurants/not-a-uuid/menu"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.restaurantId").value("Invalid format"));

        verifyNoInteractions(publicMenuService);
    }

    @Test
    void returnsBadRequestWhenJsonBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request body is missing or malformed"));

        verifyNoInteractions(restaurantService);
    }

    @Test
    void returnsBadRequestWhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request body is missing or malformed"));

        verifyNoInteractions(restaurantService);
    }

    @Test
    void returnsNotFoundErrorWhenRestaurantDoesNotExist() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        when(restaurantService.getRestaurantById(restaurantId))
                .thenThrow(new RestaurantNotFoundException(restaurantId));

        mockMvc.perform(get("/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESTAURANT_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Restaurant with id " + restaurantId + " was not found"));

        verify(restaurantService).getRestaurantById(restaurantId);
    }

    @Test
    void returnsNotFoundErrorWhenUpdatingRestaurantThatDoesNotExist() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                null,
                null,
                null,
                "+30 210 987 6543",
                null,
                null,
                null
        );

        when(restaurantService.updateRestaurant(eq(restaurantId), any(UpdateRestaurantRequest.class)))
                .thenThrow(new RestaurantNotFoundException(restaurantId));

        mockMvc.perform(patch("/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "+30 210 987 6543"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESTAURANT_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Restaurant with id " + restaurantId + " was not found"));

        verify(restaurantService).updateRestaurant(restaurantId, request);
    }

    private RestaurantResponse restaurantResponse(UUID id, String name, String address) {
        return new RestaurantResponse(
                id,
                name,
                address,
                true,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
