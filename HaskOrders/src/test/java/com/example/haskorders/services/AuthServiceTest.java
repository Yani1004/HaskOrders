package com.example.haskorders.services;

import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    public void authenticate_Successful_Login() {
        User mockUser = new User();
        String username = "mocker";
        String password = "12344321";
        mockUser.setUsername(username);
        mockUser.setPassword(password);

        when(userRepository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.of(mockUser));

        User result = authService.authenticate(username, password);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    public void authenticate_Failure_Login() {
        User mockUser = new User();
        String username = "mocker";
        String password = "12344321";
        mockUser.setUsername(username);
        mockUser.setPassword(password);

        when(userRepository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.empty());

        User result = authService.authenticate(username, password);

        assertNull(result);
    }
}
