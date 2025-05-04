package com.example.haskorders.services;

import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {
    private UserRepository userRepository;
    private RegistrationService registrationService;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        registrationService = new RegistrationService(userRepository);
    }

    @Test
    public void validateAndRegister_MissingFields_ReturnsError() {
        String result = registrationService.validateAndRegister("", "pass1234", "pass1234", "email@test.com", "Name", "1234567890", "Address");
        assertEquals("All fields are required!", result);
    }

    @Test
    public void validateAndRegister_InvalidPassword_ReturnsError() {
        String result = registrationService.validateAndRegister("user", "short", "short", "email@test.com", "Name", "1234567890", "Address");
        assertEquals("Password must be at least 8 characters!", result);
    }

    @Test
    public void validateAndRegister_PasswordsDoNotMatch_ReturnsError() {
        String result = registrationService.validateAndRegister("user", "pass1234", "diff1234", "email@test.com", "Name", "1234567890", "Address");
        assertEquals("Passwords do not match!", result);
    }

    @Test
    public void validateAndRegister_InvalidEmail_ReturnsError() {
        String result = registrationService.validateAndRegister("user", "pass1234", "pass1234", "invalidemail", "Name", "1234567890", "Address");
        assertEquals("Please enter a valid email address!", result);
    }

    @Test
    public void validateAndRegister_InvalidPhone_ReturnsError() {
        String result = registrationService.validateAndRegister("user", "pass1234", "pass1234", "email@test.com", "Name", "12345", "Address");
        assertEquals("Please enter a valid phone number (10 digits minimum)!", result);
    }

    @Test
    public void validateAndRegister_UsernameAlreadyExists_ReturnsError() {
        when(userRepository.existsByUsernameIgnoreCase("existingUser")).thenReturn(true);

        String result = registrationService.validateAndRegister("existingUser", "password123", "password123", "email@test.com", "Name", "1234567890", "Address");
        assertEquals("Username already exists!", result);
    }

    @Test
    public void validateAndRegister_AllValid_ReturnsNull() {
        when(userRepository.existsByUsernameIgnoreCase("newUser")).thenReturn(false);

        String result = registrationService.validateAndRegister("newUser", "password123", "password123", "email@test.com", "Name", "1234567890", "Address");
        assertNull(result);
        verify(userRepository).save(any(User.class));
    }
}

