package com.fastbite.restaurantservice.restaurant.service;

import com.fastbite.restaurantservice.restaurant.dto.CreateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantDetailsResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantPageResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantSummaryResponse;
import com.fastbite.restaurantservice.restaurant.dto.RestaurantResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantAccessDeniedException;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantNotFoundException;
import com.fastbite.restaurantservice.restaurant.mapper.PublicRestaurantMapper;
import com.fastbite.restaurantservice.restaurant.mapper.RestaurantMapper;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import com.fastbite.restaurantservice.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fastbite.restaurantservice.restaurant.security.TestUsers.ADMIN;
import static com.fastbite.restaurantservice.restaurant.security.TestUsers.OTHER_OWNER;
import static com.fastbite.restaurantservice.restaurant.security.TestUsers.OWNER;
import static com.fastbite.restaurantservice.restaurant.security.TestUsers.OWNER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @Mock
    private PublicRestaurantMapper publicRestaurantMapper;

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
                "Athens Municipality",
                "Attica",
                "10552"
        );
        Restaurant restaurantToSave = Restaurant.builder()
                .name(request.name())
                .address(request.address())
                .description(request.description())
                .phone(request.phone())
                .city(request.city())
                .municipality(request.municipality())
                .region(request.region())
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
                .municipality(request.municipality())
                .region(request.region())
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
                request.municipality(),
                request.region(),
                request.postalCode(),
                null,
                null
        );

        when(restaurantMapper.toEntity(request, OWNER_ID)).thenReturn(restaurantToSave);
        when(restaurantRepository.save(restaurantToSave)).thenReturn(savedRestaurant);
        when(restaurantMapper.toResponse(savedRestaurant)).thenReturn(expectedResponse);

        RestaurantResponse result = restaurantService.createRestaurant(request, OWNER_ID);

        assertSame(expectedResponse, result);
        verify(restaurantMapper).toEntity(request, OWNER_ID);
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
                null,
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

        RestaurantResponse result = restaurantService.updateRestaurant(restaurantId, request, ADMIN);

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
    void returnsPublicRestaurantPageUsingFiltersAndPageable() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .name("FastBite Athens")
                .address("Athens, Greece")
                .city("Athens")
                .municipality("Athens Municipality")
                .region("Attica")
                .build();
        PublicRestaurantSummaryResponse summary = new PublicRestaurantSummaryResponse(
                restaurantId,
                "FastBite Athens",
                "Fast food restaurant",
                "Athens",
                "Athens Municipality",
                "Attica"
        );
        Pageable pageable = PageRequest.of(1, 10);
        PageImpl<Restaurant> restaurantPage = new PageImpl<>(
                List.of(restaurant),
                pageable,
                21
        );

        when(restaurantRepository.searchActiveRestaurants(
                "Athens",
                "Athens Municipality",
                "Attica",
                pageable
        )).thenReturn(restaurantPage);
        when(publicRestaurantMapper.toResponse(restaurant)).thenReturn(summary);

        PublicRestaurantPageResponse result = restaurantService.searchPublicRestaurants(
                "Athens",
                "Athens Municipality",
                "Attica",
                pageable
        );

        assertEquals(List.of(summary), result.content());
        assertEquals(1, result.page());
        assertEquals(10, result.size());
        assertEquals(21, result.totalElements());
        assertEquals(3, result.totalPages());
        verify(restaurantRepository).searchActiveRestaurants(
                "Athens",
                "Athens Municipality",
                "Attica",
                pageable
        );
        verify(publicRestaurantMapper).toResponse(restaurant);
    }

    @Test
    void returnsPublicRestaurantDetailsWhenRestaurantIsActive() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .name("FastBite Athens")
                .address("Athens, Greece")
                .build();
        PublicRestaurantDetailsResponse expectedResponse = new PublicRestaurantDetailsResponse(
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

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(restaurant));
        when(publicRestaurantMapper.toDetailsResponse(restaurant))
                .thenReturn(expectedResponse);

        PublicRestaurantDetailsResponse result = restaurantService
                .getPublicRestaurantDetails(restaurantId);

        assertSame(expectedResponse, result);
        verify(restaurantRepository).findById(restaurantId);
        verify(publicRestaurantMapper).toDetailsResponse(restaurant);
    }

    @Test
    void throwsNotFoundWhenRestaurantIsInactiveForPublicDetails() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant inactiveRestaurant = Restaurant.builder()
                .id(restaurantId)
                .name("FastBite Athens")
                .address("Athens, Greece")
                .active(false)
                .build();

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.of(inactiveRestaurant));

        assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantService.getPublicRestaurantDetails(restaurantId)
        );

        verify(restaurantRepository).findById(restaurantId);
        verifyNoInteractions(publicRestaurantMapper);
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
                null,
                null,
                null
        );

        when(restaurantRepository.findById(restaurantId))
                .thenReturn(Optional.empty());

        assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantService.updateRestaurant(restaurantId, request, ADMIN)
        );

        verify(restaurantRepository).findById(restaurantId);
        verifyNoInteractions(restaurantMapper);
    }

    @Test
    void allowsOwnerToUpdateOwnRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = ownedRestaurant(restaurantId, OWNER_ID);
        UpdateRestaurantRequest request = renameRequest();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        restaurantService.updateRestaurant(restaurantId, request, OWNER);

        verify(restaurantMapper).updateEntity(request, restaurant);
    }

    @Test
    void rejectsUpdateOfRestaurantOwnedByAnotherUser() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = ownedRestaurant(restaurantId, OWNER_ID);
        UpdateRestaurantRequest request = renameRequest();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThrows(
                RestaurantAccessDeniedException.class,
                () -> restaurantService.updateRestaurant(restaurantId, request, OTHER_OWNER)
        );

        verify(restaurantMapper, never()).updateEntity(request, restaurant);
    }

    @Test
    void rejectsOwnerUpdateOfRestaurantWithoutOwner() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant restaurant = ownedRestaurant(restaurantId, null);
        UpdateRestaurantRequest request = renameRequest();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThrows(
                RestaurantAccessDeniedException.class,
                () -> restaurantService.updateRestaurant(restaurantId, request, OWNER)
        );
    }

    private Restaurant ownedRestaurant(UUID restaurantId, UUID ownerId) {
        return Restaurant.builder()
                .id(restaurantId)
                .name("FastBite Athens")
                .address("Athens, Greece")
                .ownerId(ownerId)
                .build();
    }

    private UpdateRestaurantRequest renameRequest() {
        return new UpdateRestaurantRequest("New name", null, null, null, null, null, null, null, null);
    }
}
