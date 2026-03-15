package com.payroll.payroll.controller;

import com.payroll.payroll.model.Employee;
import com.payroll.payroll.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Show all employees
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "employee-list";
    }

    // Show add form
    @GetMapping("/employees/add")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "add-employee";
    }

    // Save employee
    @PostMapping("/employees/save")
    public String saveEmployee(@ModelAttribute Employee employee) {

        // Payroll Logic
        double netSalary = employee.getSalary() + employee.getBonus() - employee.getTax();
        employee.setNetSalary(netSalary);

        employeeRepository.save(employee);

        return "redirect:/employees";
    }

    //  Show Update Form
    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id: " + id));

        model.addAttribute("employee", employee);
        return "update-employee";
    }

    // Delete Employee
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }
}
