package com.example.haskorders.client;

import com.example.haskorders.repositories.*;
import com.example.haskorders.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@RequiredArgsConstructor()
public class SwingApp implements ApplicationRunner {

    private final DelivererService delivererService;
    private final AdminService adminService;
    private final RegistrationService registrationService;
    private final AuthService authService;
    private final EmployeeService employeeService;
    private final OrderService orderService;

    @Override
    public void run(org.springframework.boot.ApplicationArguments args){
        System.setProperty("java.awt.headless", "false");
        SwingUtilities.invokeLater(() -> {
            new LoginPage(delivererService, adminService, registrationService, authService, employeeService, orderService);
        });
    }
}
