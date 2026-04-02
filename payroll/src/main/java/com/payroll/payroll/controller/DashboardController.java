package com.payroll.payroll.controller;

import com.payroll.payroll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());
        return "dashboard/admin";
    }

    @GetMapping("/hr/dashboard")
    public String hrDashboard(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());
        return "dashboard/hr";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(Model model, Authentication auth) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("employee", user.getEmployee());
        return "dashboard/employee";
    }
}