package com.fastbite.identityservice.identity.service;

import com.fastbite.identityservice.identity.dto.LoginRequest;
import com.fastbite.identityservice.identity.dto.LoginResponse;
import com.fastbite.identityservice.identity.dto.RegisterRequest;
import com.fastbite.identityservice.identity.dto.UserResponse;
import com.fastbite.identityservice.identity.exception.DefaultRoleNotFoundException;
import com.fastbite.identityservice.identity.exception.EmailAlreadyExistsException;
import com.fastbite.identityservice.identity.exception.InvalidCredentialsException;
import com.fastbite.identityservice.identity.mapper.UserMapper;
import com.fastbite.identityservice.identity.model.Role;
import com.fastbite.identityservice.identity.model.User;
import com.fastbite.identityservice.identity.repository.RoleRepository;
import com.fastbite.identityservice.identity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder, new UserMapper(), jwtService);
    }

    @Test
    void registersCustomerWithNormalizedEmailAndHashedPassword() {
        Role customerRole = Role.builder().name("CUSTOMER").build();
        RegisterRequest request = new RegisterRequest("  Maria Papadopoulou ", " Maria@Example.COM ", "correct-horse-battery");

        when(userRepository.existsByEmail("maria@example.com")).thenReturn(false);
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("correct-horse-battery")).thenReturn("hashed-password");
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.register(request);

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(savedUser.capture());

        assertThat(savedUser.getValue().getEmail()).isEqualTo("maria@example.com");
        assertThat(savedUser.getValue().getFullName()).isEqualTo("Maria Papadopoulou");
        assertThat(savedUser.getValue().getPasswordHash()).isEqualTo("hashed-password");
        assertThat(savedUser.getValue().getRoles()).containsExactly(customerRole);

        assertThat(response.email()).isEqualTo("maria@example.com");
        assertThat(response.roles()).containsExactly("CUSTOMER");
    }

    @Test
    void rejectsEmailThatIsAlreadyRegistered() {
        RegisterRequest request = new RegisterRequest("Maria Papadopoulou", "MARIA@example.com", "correct-horse-battery");

        when(userRepository.existsByEmail("maria@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void failsWhenDefaultRoleIsMissing() {
        RegisterRequest request = new RegisterRequest("Maria Papadopoulou", "maria@example.com", "correct-horse-battery");

        when(userRepository.existsByEmail("maria@example.com")).thenReturn(false);
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(DefaultRoleNotFoundException.class);

        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void returnsBearerTokenWhenCredentialsAreCorrect() {
        User user = existingUser();
        LoginRequest request = new LoginRequest(" Maria@Example.COM ", "correct-horse-battery");

        when(userRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correct-horse-battery", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("signed-jwt");
        when(jwtService.getExpirationSeconds()).thenReturn(900L);

        LoginResponse response = userService.login(request);

        assertThat(response.accessToken()).isEqualTo("signed-jwt");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900L);
    }

    @Test
    void rejectsWrongPasswordWithoutIssuingToken() {
        User user = existingUser();
        LoginRequest request = new LoginRequest("maria@example.com", "wrong-password");

        when(userRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void rejectsUnknownEmailWithSameErrorAsWrongPassword() {
        LoginRequest request = new LoginRequest("nobody@example.com", "any-password");

        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    private User existingUser() {
        return User.builder()
                .fullName("Maria Papadopoulou")
                .email("maria@example.com")
                .passwordHash("hashed-password")
                .roles(new HashSet<>(Set.of(Role.builder().name("CUSTOMER").build())))
                .build();
    }
}
