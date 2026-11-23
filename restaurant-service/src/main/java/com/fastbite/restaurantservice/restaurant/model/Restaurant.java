package com.fastbite.restaurantservice.restaurant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String municipality;

    @Column(length = 100)
    private String region;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "owner_id")
    private UUID ownerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public void updateDetails(
            String name,
            String address,
            String description,
            String phone,
            String city,
            String municipality,
            String region,
            String postalCode,
            Boolean active
    ) {
        if (name != null) {
            this.name = name;
        }

        if (address != null) {
            this.address = address;
        }

        if (description != null) {
            this.description = description;
        }

        if (phone != null) {
            this.phone = phone;
        }

        if (city != null) {
            this.city = city;
        }

        if (municipality != null) {
            this.municipality = municipality;
        }

        if (region != null) {
            this.region = region;
        }

        if (postalCode != null) {
            this.postalCode = postalCode;
        }

        if (active != null) {
            this.active = active;
        }
    }

    public boolean isOwnedBy(UUID userId) {
        return ownerId != null && ownerId.equals(userId);
    }
}
