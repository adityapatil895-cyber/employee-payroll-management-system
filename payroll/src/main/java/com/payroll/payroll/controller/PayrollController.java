package com.payroll.payroll.controller;

import com.payroll.payroll.model.Employee;
import com.payroll.payroll.repository.EmployeeRepository;
import com.payroll.payroll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PayrollController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    // Admin/HR view all payroll
    @GetMapping("/payroll")
    public String showPayroll(Model model) {

        List<Employee> employees = employeeRepository.findAll();

        double totalNetSalary = employees.stream()
                .mapToDouble(Employee::getNetSalary)
                .sum();

        double totalDeductions = employees.stream()
                .mapToDouble(e -> e.getTax() + e.getPf())
                .sum();

        model.addAttribute("employees", employees);
        model.addAttribute("totalNetSalary", totalNetSalary);
        model.addAttribute("totalDeductions", totalDeductions);

        return "payroll/list"; // ✅ matches your file path
    }

    // View one employee payslip detail
    @GetMapping("/payroll/detail/{id}")
    public String viewPayrollDetail(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        model.addAttribute("employee", employee);
        return "payroll/detail";
    }

    // Employee views own payslip
    @GetMapping("/employee/payslip")
    public String viewOwnPayslip(Model model, Authentication auth) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("employee", user.getEmployee());
        return "payroll/detail";
    }
}