package bg.haskorders.delivery.model.user;


import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class User {

    private Long userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Role role;
}