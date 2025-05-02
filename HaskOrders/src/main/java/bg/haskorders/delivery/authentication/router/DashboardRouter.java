package bg.haskorders.delivery.authentication.router;

import bg.haskorders.delivery.Main;
import bg.haskorders.delivery.admin.AdminDashboard;
import bg.haskorders.delivery.client.ClientDashboard;
import bg.haskorders.delivery.deliverer.DelivererDashboard;
import bg.haskorders.delivery.employee.EmployeeDashboard;
import bg.haskorders.delivery.model.user.User;

import java.util.ArrayList;

public class DashboardRouter {
    public static void route(User user) {
        switch (user.getRole()) {
            case ADMIN -> new AdminDashboard((ArrayList<User>) Main.userList);
            case EMPLOYEE -> new EmployeeDashboard(user);
            case DELIVERER -> new DelivererDashboard(user);
            case CLIENT -> new ClientDashboard(user, Main.restaurantRepository, Main.productRepository);
        }
    }
}
