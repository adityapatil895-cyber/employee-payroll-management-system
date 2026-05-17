package com.payroll.payroll.config;

import com.payroll.payroll.model.Role;
import com.payroll.payroll.model.User;
import com.payroll.payroll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {


        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
            System.out.println(" Admin created → username: admin | password: admin123");
        }


        if (userRepository.findByUsername("hr").isEmpty()) {
            User hr = new User();
            hr.setUsername("hr");
            hr.setPassword(passwordEncoder.encode("hr123"));
            hr.setRole(Role.ROLE_HR);
            userRepository.save(hr);
            System.out.println(" HR created → username: hr | password: hr123");
        }
    }
}
