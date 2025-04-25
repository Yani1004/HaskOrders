package bg.haskorders.delivery.authentication.service;

import bg.haskorders.delivery.model.user.User;

import java.util.List;

public class CredentialsValidator {
    public static boolean isValid(String username, String password) {
        return username != null && !username.trim().isEmpty()
                && password != null && !password.trim().isEmpty();
    }


    public static boolean isEmailValid(String email) {
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public static boolean isPhoneValid(String phone) {
        return phone.length() == 10 && phone.matches("\\d+");
    }

    public static boolean isUsernameUnique(String username, List<User> users) {
        return users.stream().noneMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    public static boolean doPasswordsMatch(String pass1, String pass2) {
        return pass1.equals(pass2);
    }
}
