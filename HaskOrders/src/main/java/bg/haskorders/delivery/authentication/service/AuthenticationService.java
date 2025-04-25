package bg.haskorders.delivery.authentication.service;

import bg.haskorders.delivery.model.user.User;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class AuthenticationService {
    private final List<User> users;

    public Optional<User> authenticate(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) &&
                        user.getPassword().equals(password))
                .findFirst();
    }
}
