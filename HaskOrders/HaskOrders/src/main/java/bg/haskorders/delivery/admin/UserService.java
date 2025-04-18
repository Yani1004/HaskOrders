package bg.haskorders.delivery.admin;


import bg.haskorders.delivery.model.user.Role;
import bg.haskorders.delivery.model.user.User;

import javax.swing.*;
import java.util.ArrayList;

public class UserService {

    private ArrayList<User> usersList;

    public UserService(ArrayList<User> usersList) {
        this.usersList = usersList;
    }

    // Get the user by username
    public User getUserByUsername(String username) {
        for (User user : usersList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Update the role of the user
    public boolean updateUserRole(String username, String newRole) {
        User user = getUserByUsername(username);
        if (user == null || username.equals("admin")) {
            return false;
        }
        user.setRole(Role.valueOf(newRole));
        return true;
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }
}
