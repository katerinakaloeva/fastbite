package com.fastbite.identityservice.identity.service;

import com.fastbite.identityservice.identity.dto.RegisterRequest;
import com.fastbite.identityservice.identity.dto.UserResponse;
import com.fastbite.identityservice.identity.exception.DefaultRoleNotFoundException;
import com.fastbite.identityservice.identity.exception.EmailAlreadyExistsException;
import com.fastbite.identityservice.identity.mapper.UserMapper;
import com.fastbite.identityservice.identity.model.Role;
import com.fastbite.identityservice.identity.model.User;
import com.fastbite.identityservice.identity.repository.RoleRepository;
import com.fastbite.identityservice.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String DEFAULT_ROLE = "CUSTOMER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        Role customerRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(DefaultRoleNotFoundException::new);

        String passwordHash = passwordEncoder.encode(request.password());

        RegisterRequest normalizedRequest = new RegisterRequest(
                request.fullName().trim(),
                email,
                request.password()
        );

        User user = userMapper.toEntity(normalizedRequest, passwordHash, customerRole);
        User savedUser = userRepository.saveAndFlush(user);

        return userMapper.toResponse(savedUser);
    }
}
