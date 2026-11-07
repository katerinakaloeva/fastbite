package com.fastbite.restaurantservice.restaurant.service;

import com.fastbite.restaurantservice.restaurant.dto.CreateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.dto.RestaurantResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantNotFoundException;
import com.fastbite.restaurantservice.restaurant.mapper.RestaurantMapper;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import com.fastbite.restaurantservice.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    void createsRestaurant() {
        CreateRestaurantRequest request = new CreateRestaurantRequest(
                "FastBite Athens",
                "Athens, Greece",
                "Fast food restaurant",
                "+30 210 123 4567",
                "Athens",
                "10552"
        );
        Restaurant restaurantToSave = Restaurant.builder()
                .name(request.name())
                .address(request.address())
                .description(request.description())
                .phone(request.phone())
                .city(request.city())
                .postalCode(request.postalCode())
                .build();
        UUID restaurantId = UUID.randomUUID();
        Restaurant savedRestaurant = Restaurant.builder()
                .id(restaurantId)
                .name(request.name())
                .address(request.address())
                .description(request.description())
                .phone(request.phone())
                .city(request.city())
                .postalCode(request.postalCode())
                .build();
        RestaurantResponse expectedResponse = new RestaurantResponse(
                restaurantId,
                request.name(),
                request.address(),
                true,
                request.description(),
                request.phone(),
                request.city(),
                request.postalCode(),
                null,
                null
        );

        when(restaurantMapper.toEntity(request)).thenReturn(restaurantToSave);
        when(restaurantRepository.save(restaurantToSave)).thenReturn(savedRestaurant);
        when(restaurantMapper.toResponse(savedRestaurant)).thenReturn(expectedResponse);

        RestaurantResponse result = restaurantService.createRestaurant(request);

        assertSame(expectedResponse, result);
        verify(restaurantMapper).toEntity(request);
        verify(restaurantRepository).save(restaurantToSave);
        verify(restaurantMapper).toResponse(savedRestaurant);
    }

    @Test
    void returnsRestaurantResponseWhenItExists() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .name("FastBite Athens")
                .address("Athens, Greece")
                .build();
        RestaurantResponse expectedResponse = new RestaurantResponse(
                restaurantId,
                "FastBite Athens",
                "Athens, Greece",
                true,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toResponse(restaurant)).thenReturn(expectedResponse);

        RestaurantResponse result = restaurantService.getRestaurantById(restaurantId);

        assertSame(expectedResponse, result);
        verify(restaurantRepository).findById(restaurantId);
        verify(restaurantMapper).toResponse(restaurant);
    }

    @Test
    void updatesRestaurantWhenItExists() {
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
        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .name("FastBite Athens")
                .address("Athens, Greece")
                .build();
        RestaurantResponse expectedResponse = new RestaurantResponse(
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

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toResponse(restaurant)).thenReturn(expectedResponse);

        RestaurantResponse result = restaurantService.updateRestaurant(restaurantId, request);

        assertSame(expectedResponse, result);
        verify(restaurantRepository).findById(restaurantId);
        verify(restaurantMapper).updateEntity(request, restaurant);
        verify(restaurantMapper).toResponse(restaurant);
    }

    @Test
    void returnsAllRestaurantResponses() {
        Restaurant athensRestaurant = Restaurant.builder()
                .name("FastBite Athens")
                .address("Athens, Greece")
                .build();
        Restaurant thessalonikiRestaurant = Restaurant.builder()
                .name("FastBite Thessaloniki")
                .address("Thessaloniki, Greece")
                .build();
        List<Restaurant> restaurants = List.of(athensRestaurant, thessalonikiRestaurant);

        RestaurantResponse athensResponse = new RestaurantResponse(
                UUID.randomUUID(),
                "FastBite Athens",
                "Athens, Greece",
                true,
                null,
                null,
                null,
                null,
                null,
                null
        );
        RestaurantResponse thessalonikiResponse = new RestaurantResponse(
                UUID.randomUUID(),
                "FastBite Thessaloniki",
                "Thessaloniki, Greece",
                true,
                null,
                null,
                null,
                null,
                null,
                null
        );
        List<RestaurantResponse> expectedResponses = List.of(athensResponse, thessalonikiResponse);

        when(restaurantRepository.findAll()).thenReturn(restaurants);
        when(restaurantMapper.toResponse(athensRestaurant)).thenReturn(athensResponse);
        when(restaurantMapper.toResponse(thessalonikiRestaurant)).thenReturn(thessalonikiResponse);

        List<RestaurantResponse> result = restaurantService.getAllRestaurants();

        assertEquals(expectedResponses, result);
        verify(restaurantRepository).findAll();
        verify(restaurantMapper).toResponse(athensRestaurant);
        verify(restaurantMapper).toResponse(thessalonikiRestaurant);
    }

    @Test
    void throwsExceptionWhenRestaurantDoesNotExist() {
        UUID restaurantId = UUID.randomUUID();

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.empty());

        RestaurantNotFoundException exception = assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantService.getRestaurantById(restaurantId)
        );

        assertEquals(
                "Restaurant with id " + restaurantId + " was not found",
                exception.getMessage()
        );
        verify(restaurantRepository).findById(restaurantId);
        verifyNoInteractions(restaurantMapper);
    }

    @Test
    void throwsExceptionWhenUpdatingRestaurantThatDoesNotExist() {
        UUID restaurantId = UUID.randomUUID();
        UpdateRestaurantRequest request = new UpdateRestaurantRequest(
                "FastBite Piraeus",
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.empty());

        assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantService.updateRestaurant(restaurantId, request)
        );

        verify(restaurantRepository).findById(restaurantId);
        verifyNoInteractions(restaurantMapper);
    }
}
