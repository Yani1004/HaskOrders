package bg.haskorders.delivery;

import bg.haskorders.delivery.auth.LoginPage;
import bg.haskorders.delivery.model.user.User;

import javax.swing.*;
import java.util.ArrayList;


import static bg.haskorders.delivery.auth.LoginPage.initializeUsers;


public class Main {
    public static void main(String[] args) {
        ArrayList<User> userList = initializeUsers();
      SwingUtilities.invokeLater(() -> new LoginPage(userList));

    }
}