package com.example.haskorders.services;

import com.example.haskorders.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidationServiceTest {

    @Test
    public void IsEmailValid() {
        assertTrue(ValidationService.isEmailValid("test@example.com"));
        assertFalse(ValidationService.isEmailValid("invalid-email"));
    }

    @Test
    public void IsPhoneValid() {
        assertTrue(ValidationService.isPhoneValid("0884158073"));
        assertFalse(ValidationService.isPhoneValid("12345"));
    }

    @Test
    public void IsPasswordValid() {
        assertTrue(ValidationService.isPasswordValid("password"));
        assertFalse(ValidationService.isPasswordValid("pass"));
    }

    @Test
    public void DoPasswordsMatch() {
        assertTrue(ValidationService.doPasswordsMatch("password", "password"));
        assertFalse(ValidationService.doPasswordsMatch("password", "invalid-password"));
    }

    @Test
    public void IsUsernameUnique() {
        UserRepository mockRepository = mock(UserRepository.class);

        when(mockRepository.existsByUsernameIgnoreCase("uniqueUsername")).thenReturn(false);
        when(mockRepository.existsByUsernameIgnoreCase("existingUsername")).thenReturn(true);

        assertTrue(ValidationService.isUsernameUnique("uniqueUsername", mockRepository));
        assertFalse(ValidationService.isUsernameUnique("existingUsername", mockRepository));
    }
}
