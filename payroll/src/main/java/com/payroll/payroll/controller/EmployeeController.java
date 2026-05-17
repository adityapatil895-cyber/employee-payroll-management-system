package com.payroll.payroll.controller;

import com.payroll.payroll.service.PayrollConfigService;
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
import com.payroll.payroll.repository.AttendanceRepository;

import java.io.IOException;

@Controller
public class EmployeeController {
    @Autowired
    private PayrollConfigService payrollConfigService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AttendanceRepository attendanceRepository;

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

        double hraRate = payrollConfigService.get("hra", 40.0) / 100;
        double allowancesRate = payrollConfigService.get("allowances", 20.0) / 100;
        double pfRate = payrollConfigService.get("pf", 12.0) / 100;
        double taxRate = payrollConfigService.get("tax", 10.0) / 100;

        double hra = basic * hraRate;
        double allowances = basic * allowancesRate;
        double pf = basic * pfRate;

        double hourlyRate = basic / 160;
        double overtimePay = employee.getOvertimeHours() * hourlyRate;

        double grossSalary = basic + hra + allowances + overtimePay;
        double tax = grossSalary * taxRate;
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
        Employee employee = employeeRepository.findById(id).orElseThrow();


        attendanceRepository.findByEmployee(employee)
                .forEach(a -> attendanceRepository.delete(a));


        userRepository.findAll().stream()
                .filter(u -> u.getEmployee() != null &&
                        u.getEmployee().getId().equals(id))
                .forEach(u -> {
                    u.setEmployee(null);
                    userRepository.save(u);
                    userRepository.delete(u);
                });


        employeeRepository.deleteById(id);

        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}/payslip")
    public void downloadPayslip(@PathVariable Long id,
                                HttpServletResponse response,
                                org.springframework.security.core.Authentication auth) throws IOException {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id: " + id));


        var user = userRepository.findByUsername(auth.getName()).orElseThrow();


        if (user.getRole().name().equals("ROLE_EMPLOYEE")) {
            if (user.getEmployee() == null || !user.getEmployee().getId().equals(id)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }
        }

        pdfService.generatePayslip(employee, response);
    }
}