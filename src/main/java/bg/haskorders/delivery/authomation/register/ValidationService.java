package bg.haskorders.delivery.authomation.register;


import bg.haskorders.delivery.model.user.User;

import java.util.List;

public class ValidationService {

    public static boolean isEmailValid(String email) {
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public static boolean isPhoneValid(String phone) {
        return phone.length() >= 10;
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
