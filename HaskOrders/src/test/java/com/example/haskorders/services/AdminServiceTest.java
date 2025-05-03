package com.example.haskorders.services;

import com.example.haskorders.entities.user.Role;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminServiceTest {
    private UserRepository userRepository;
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        adminService = new AdminService(userRepository);
    }

    @Test
    public void getAllUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = adminService.getAllUsers();

        assertEquals(2, allUsers.size());
    }

    @Test
    public void updateUserRole() {
        User user = new User();
        user.setUsername("testusername");

        when(userRepository.findByUsername("testusername")).thenReturn(Optional.of(user));

        Optional<User> updatedUser = adminService.updateUserRole("testusername", Role.DELIVERER);

        assertTrue(updatedUser.isPresent());
        assertEquals(Role.DELIVERER, updatedUser.get().getRole());
        verify(userRepository).save(user);
    }

    @Test
    public void updateUserRole_Admin_NotAllowed() {
        Optional<User> updatedUser = adminService.updateUserRole("admin", Role.CLIENT);

        assertFalse(updatedUser.isPresent());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void updateUserRole_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> updatedUser = adminService.updateUserRole("unknown", Role.CLIENT);

        assertFalse(updatedUser.isPresent());
    }
}
