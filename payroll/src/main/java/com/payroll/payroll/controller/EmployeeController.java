package com.payroll.payroll.controller;

import com.payroll.payroll.model.Employee;
import com.payroll.payroll.model.Role;
import com.payroll.payroll.model.User;
import com.payroll.payroll.repository.EmployeeRepository;
import com.payroll.payroll.repository.UserRepository;
import com.payroll.payroll.service.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "employee-list";
    }

    @GetMapping("/employees/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "add-employee";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(@ModelAttribute Employee employee) {

        double basic = employee.getBasicSalary();

        double hra = basic * 0.40;
        double allowances = basic * 0.20;
        double pf = basic * 0.12;

        double hourlyRate = basic / 160;
        double overtimePay = employee.getOvertimeHours() * hourlyRate;

        double grossSalary = basic + hra + allowances + overtimePay;
        double tax = grossSalary * 0.10;
        double netSalary = grossSalary - tax - pf;

        employee.setHra(hra);
        employee.setAllowances(allowances);
        employee.setPf(pf);
        employee.setTax(tax);
        employee.setOvertimePay(overtimePay);
        employee.setGrossSalary(grossSalary);
        employee.setNetSalary(netSalary);

        Employee savedEmployee = employeeRepository.save(employee);

        String username = employee.getName().toLowerCase().replace(" ", "");

        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode("emp123"));
            user.setRole(Role.ROLE_EMPLOYEE);
            user.setEmployee(savedEmployee);
            userRepository.save(user);
        }

        return "redirect:/employees";
    }

    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id: " + id));
        model.addAttribute("employee", employee);
        return "update-employee";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}/payslip")
    public void downloadPayslip(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id: " + id));
        pdfService.generatePayslip(employee, response);
    }
}