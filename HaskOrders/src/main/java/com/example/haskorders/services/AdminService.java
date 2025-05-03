package com.example.haskorders.services;

import com.example.haskorders.entities.user.Role;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> updateUserRole(String username, Role role) {
        if(username.equals("admin")) {
            return Optional.empty();
        }
        Optional<User> optionalUser = userRepository.findByUsername(username);
        optionalUser.ifPresent(user -> {
            user.setRole(role);
            userRepository.save(user);
        });
        return optionalUser;
    }
}
