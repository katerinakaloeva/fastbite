package com.fastbite.identityservice.identity.exception;

import com.fastbite.identityservice.identity.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void returnsConflictWithCodeAndMessageForExistingEmail() throws Exception {
        mockMvc.perform(post("/test/email"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Email is already registered"))
                .andExpect(jsonPath("$.path").value("/test/email"));
    }

    @Test
    void returnsInternalErrorWithCodeAndMessageForMissingDefaultRole() throws Exception {
        mockMvc.perform(post("/test/default-role"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("DEFAULT_ROLE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Default customer role is not configured"));
    }

    @Test
    void returnsValidationErrorsForInvalidRequest() throws Exception {
        mockMvc.perform(post("/test/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"","email":"bad-email","password":"short"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.fullName").value("Full name is required"))
                .andExpect(jsonPath("$.fieldErrors.email").value("Email must be valid"))
                .andExpect(jsonPath("$.fieldErrors.password")
                        .value("Password must be between 12 and 128 characters"));
    }

    @Test
    void returnsValidationCodeForMalformedJson() throws Exception {
        mockMvc.perform(post("/test/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request body is missing or malformed"));
    }

    @RestController
    static class TestController {

        @PostMapping("/test/email")
        void email() {
            throw new EmailAlreadyExistsException();
        }

        @PostMapping("/test/default-role")
        void defaultRole() {
            throw new DefaultRoleNotFoundException();
        }

        @PostMapping("/test/register")
        void register(@Valid @RequestBody RegisterRequest request) {
        }
    }
}
