package com.fastbite.restaurantservice.restaurant.controller;

import com.fastbite.restaurantservice.config.SecurityConfig;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantDetailsResponse;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantNotFoundException;
import com.fastbite.restaurantservice.restaurant.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicRestaurantController.class)
@Import(SecurityConfig.class)
class PublicRestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private RestaurantService restaurantService;

    @Test
    void returnsPublicRestaurantDetailsWithoutAdminFields() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        PublicRestaurantDetailsResponse response = new PublicRestaurantDetailsResponse(
                restaurantId,
                "FastBite Athens",
                "Fast food restaurant",
                "Athens, Greece",
                "+30 210 123 4567",
                "Athens",
                "Athens Municipality",
                "Attica",
                "10552"
        );

        when(restaurantService.getPublicRestaurantDetails(restaurantId)).thenReturn(response);

        mockMvc.perform(get("/public/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                .andExpect(jsonPath("$.name").value("FastBite Athens"))
                .andExpect(jsonPath("$.address").value("Athens, Greece"))
                .andExpect(jsonPath("$.phone").value("+30 210 123 4567"))
                .andExpect(jsonPath("$.city").value("Athens"))
                .andExpect(jsonPath("$.municipality").value("Athens Municipality"))
                .andExpect(jsonPath("$.region").value("Attica"))
                .andExpect(jsonPath("$.postalCode").value("10552"))
                .andExpect(jsonPath("$.active").doesNotExist())
                .andExpect(jsonPath("$.createdAt").doesNotExist())
                .andExpect(jsonPath("$.updatedAt").doesNotExist());

        verify(restaurantService).getPublicRestaurantDetails(restaurantId);
    }

    @Test
    void returnsNotFoundWhenRestaurantIsMissingOrInactive() throws Exception {
        UUID restaurantId = UUID.randomUUID();

        when(restaurantService.getPublicRestaurantDetails(restaurantId))
                .thenThrow(new RestaurantNotFoundException(restaurantId));

        mockMvc.perform(get("/public/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESTAURANT_NOT_FOUND"));

        verify(restaurantService).getPublicRestaurantDetails(restaurantId);
    }
}
