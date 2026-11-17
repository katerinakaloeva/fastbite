package com.fastbite.restaurantservice.restaurant.service;

import com.fastbite.restaurantservice.restaurant.dto.CreateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantDetailsResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantPageResponse;
import com.fastbite.restaurantservice.restaurant.dto.PublicRestaurantSummaryResponse;
import com.fastbite.restaurantservice.restaurant.dto.RestaurantResponse;
import com.fastbite.restaurantservice.restaurant.dto.UpdateRestaurantRequest;
import com.fastbite.restaurantservice.restaurant.exception.RestaurantNotFoundException;
import com.fastbite.restaurantservice.restaurant.mapper.PublicRestaurantMapper;
import com.fastbite.restaurantservice.restaurant.mapper.RestaurantMapper;
import com.fastbite.restaurantservice.restaurant.model.Restaurant;
import com.fastbite.restaurantservice.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final PublicRestaurantMapper publicRestaurantMapper;

    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = restaurantMapper.toEntity(request);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return restaurantMapper.toResponse(savedRestaurant);
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        return restaurantMapper.toResponse(restaurant);
    }

    @Transactional(readOnly = true)
    public PublicRestaurantDetailsResponse getPublicRestaurantDetails(UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        if (!restaurant.isActive()) {
            throw new RestaurantNotFoundException(restaurantId);
        }

        return publicRestaurantMapper.toDetailsResponse(restaurant);
    }

    @Transactional
    public RestaurantResponse updateRestaurant(
            UUID restaurantId,
            UpdateRestaurantRequest request
    ) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        restaurantMapper.updateEntity(request, restaurant);

        return restaurantMapper.toResponse(restaurant);
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicRestaurantPageResponse searchPublicRestaurants(
            String city,
            String municipality,
            String region,
            Pageable pageable
    ) {
        Page<Restaurant> restaurants = restaurantRepository.searchActiveRestaurants(
                city,
                municipality,
                region,
                pageable
        );

        List<PublicRestaurantSummaryResponse> content = restaurants.getContent()
                .stream()
                .map(publicRestaurantMapper::toResponse)
                .toList();

        return new PublicRestaurantPageResponse(
                content,
                restaurants.getNumber(),
                restaurants.getSize(),
                restaurants.getTotalElements(),
                restaurants.getTotalPages()
        );
    }
}
