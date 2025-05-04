package com.example.haskorders.services;

import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password).orElse(null);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }
}
