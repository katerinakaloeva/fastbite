package com.fastbite.identityservice.identity.mapper;

import com.fastbite.identityservice.identity.dto.RegisterRequest;
import com.fastbite.identityservice.identity.dto.UserResponse;
import com.fastbite.identityservice.identity.model.Role;
import com.fastbite.identityservice.identity.model.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request, String passwordHash, Role customerRole) {
        return User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordHash)
                .roles(new HashSet<>(Set.of(customerRole)))
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                user.getCreatedAt()
        );
    }
}
