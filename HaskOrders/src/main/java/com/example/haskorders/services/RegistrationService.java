package com.example.haskorders.services;

import com.example.haskorders.entities.user.Role;
import com.example.haskorders.entities.user.User;
import com.example.haskorders.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;

    public String validateAndRegister(String username, String password, String confirmPassword,
                                      String email, String name, String phone, String address) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()
                || name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            return "All fields are required!";
        }
        if (!ValidationService.isPasswordValid(password)) {
            return "Password must be at least 8 characters!";
        }
        if (!ValidationService.doPasswordsMatch(password, confirmPassword)) {
            return "Passwords do not match!";
        }
        if (!ValidationService.isEmailValid(email)) {
            return "Please enter a valid email address!";
        }
        if (!ValidationService.isPhoneValid(phone)) {
            return "Please enter a valid phone number (10 digits minimum)!";
        }
        if (!ValidationService.isUsernameUnique(username, userRepository)) {
            return "Username already exists!";
        }

        User newUser = User.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .phone(phone)
                .role(Role.CLIENT)
                .address(address)
                .build();

        userRepository.save(newUser);
        return null; // null = success
    }
}
