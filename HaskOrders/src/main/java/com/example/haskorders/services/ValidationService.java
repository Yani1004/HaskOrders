package com.example.haskorders.services;

import com.example.haskorders.repositories.UserRepository;

public class ValidationService {
    public static boolean isEmailValid(String email) {
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public static boolean isPhoneValid(String phone) {
        return phone.length() >= 10;
    }

    public static boolean isUsernameUnique(String username, UserRepository userRepository) {
        return !userRepository.existsByUsernameIgnoreCase(username);
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    public static boolean doPasswordsMatch(String pass1, String pass2) {
        return pass1.equals(pass2);
    }
}
