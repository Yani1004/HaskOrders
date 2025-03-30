package model;

import org.mindrot.jbcrypt.BCrypt;

/*
CREATE TABLE User(
	user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(25) NOT NULL UNIQUE,
	password VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    role enum('client','employee','delivery','admin') NOT NULL DEFAULT 'client'
);*/

public class User {
    // Role constants
    public static final String ROLE_CLIENT = "Client";
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_EMPLOYEE = "Employee";
    public static final String ROLE_DELIVERER = "Deliverer";

    private Long userId;
    private String username;
    private String hashedPassword;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;
    private boolean needsApproval;

    public User(String username, String password, String name, String email, String phone, String address, String role) {
        this.username = username;
        this.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        this.name = name;
        this.email = email;
        this.phone=phone;
        this.address = address;
        this.role = role;
        this.needsApproval = false;
    }

    public User(String username, String password, String name, String email, String role) {
        this.username = username;
        this.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        this.name = name;
        this.email = email;
        this.role = role;
        this.needsApproval = false;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return hashedPassword;
    }

    public void setPassword(String password) {
        this.hashedPassword = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public boolean needsApproval() {
        return needsApproval;
    }

    public void setNeedsApproval(boolean needsApproval) {
        this.needsApproval = needsApproval;
    }
}