package bg.haskorders.delivery.admin;

import bg.haskorders.delivery.model.user.Role;
import bg.haskorders.delivery.model.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService {

    private final List<User> usersList;

    public UserService(List<User> usersList) {
        this.usersList = usersList;
    }

    @Override
    public User getUserByUsername(String username) {
        for (User user : usersList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean updateUserRole(String username, String newRole) {
        User user = getUserByUsername(username);
        if (user == null || "admin".equals(username)) {
            return false;
        }
        user.setRole(Role.valueOf(newRole));
        return true;
    }

    @Override
    public List<User> getUsersList() {
        return usersList;
    }
}
