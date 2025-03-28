import java.awt.*;

public class User {
    public String username;
    public String password;
    public String name;
    public String email;
    public String role;

    public User(String username, String password, String name, String email, String role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setEmail(String email) {
        this.email = email;

    }
    public String getEmail() {
        return email;
    }


    @Override
    public String toString() {
        return " Username: " + username + " Password: " + password + " Name: " + name + " Role: " + role;
    }

}
