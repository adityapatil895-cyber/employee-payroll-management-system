package com.payroll.payroll.controller;

import com.payroll.payroll.repository.AttendanceRepository;
import com.payroll.payroll.repository.EmployeeRepository;
import com.payroll.payroll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());


        long totalEmployees = employeeRepository.count();
        model.addAttribute("totalEmployees", totalEmployees);


        double totalPayroll = employeeRepository.findAll()
                .stream()
                .mapToDouble(e -> e.getNetSalary())
                .sum();
        model.addAttribute("totalPayroll", String.format("%.2f", totalPayroll));


        long monthlyAttendance = attendanceRepository.findAll()
                .stream()
                .filter(a -> a.getDate() != null &&
                        a.getDate().getMonth() == LocalDate.now().getMonth() &&
                        a.getDate().getYear() == LocalDate.now().getYear())
                .count();


        if (monthlyAttendance == 0) {
            monthlyAttendance = attendanceRepository.findAll()
                    .stream()
                    .filter(a -> a.getDate() != null &&
                            a.getDate().getMonth() == LocalDate.now().minusMonths(1).getMonth() &&
                            a.getDate().getYear() == LocalDate.now().minusMonths(1).getYear())
                    .count();
        }

        model.addAttribute("monthlyAttendance", monthlyAttendance);

        return "dashboard/admin";
    }

    @GetMapping("/hr/dashboard")
    public String hrDashboard(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());


        long presentToday = attendanceRepository.findAll()
                .stream()
                .filter(a -> a.getDate() != null &&
                        a.getDate().equals(LocalDate.now()))
                .count();
        model.addAttribute("presentToday", presentToday);


        model.addAttribute("employees", employeeRepository.findAll());

        return "dashboard/hr";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(Model model, Authentication auth) {
        var user = userRepository.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("employee", user.getEmployee());
        return "dashboard/employee";
    }
}