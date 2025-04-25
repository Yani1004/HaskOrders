package bg.haskorders.delivery.admin;


import bg.haskorders.delivery.model.user.Role;
import bg.haskorders.delivery.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class UserService {
    private List<User> users;

    public boolean updateUserRole(String username, Role newRole){
        for (User user : users) {
            if(user.getUsername().equals(username)){
                if("admin".equals(username)){
                    return false;
                }
                user.setRole(newRole);
                return true;
            }
        }
        return false;
    }



}
