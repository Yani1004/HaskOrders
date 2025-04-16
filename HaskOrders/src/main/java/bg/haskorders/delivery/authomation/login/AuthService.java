package bg.haskorders.delivery.authomation.login;

import bg.haskorders.delivery.model.user.User;

import java.util.List;

public class AuthService {
    private final List<User> users;

    public AuthService(List<User> users) {
        this.users = users;
    }

    public User authenticate(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) &&
                        user.getPassword().equals(password)) // TODO: Use hashed password
                .findFirst()
                .orElse(null);
    }
}
