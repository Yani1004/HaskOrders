package bg.haskorders.delivery.admin;

import bg.haskorders.delivery.model.user.User;
import java.util.List;

public interface IUserService {
    User getUserByUsername(String username);
    boolean updateUserRole(String username, String newRole);
    List<User> getUsersList();
}