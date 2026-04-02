package com.payroll.payroll.controller;

import com.payroll.payroll.model.*;
import com.payroll.payroll.repository.EmployeeRepository;
import com.payroll.payroll.repository.UserRepository;
import com.payroll.payroll.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    // ─── ADMIN & HR ───────────────────────────────────────────

    // View all attendance
    @GetMapping("/attendance")
    public String viewAll(Model model) {
        model.addAttribute("attendanceList", attendanceService.getAllAttendance());
        return "attendance/list";
    }

    // Show mark attendance form (HR)
    @GetMapping("/attendance/mark")
    public String showMarkForm(Model model) {
        model.addAttribute("attendance", new Attendance());
        model.addAttribute("employees", attendanceService.getAllEmployees());
        model.addAttribute("statuses", AttendanceStatus.values());
        model.addAttribute("today", LocalDate.now());
        return "attendance/mark";
    }

    // Submit mark attendance form (HR)
    @PostMapping("/attendance/mark")
    public String markAttendance(@ModelAttribute Attendance attendance) {
        attendanceService.markAttendance(attendance);
        return "redirect:/attendance";
    }

    // Show edit form (HR override)
    @GetMapping("/attendance/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("attendance", attendanceService.getAttendanceById(id));
        model.addAttribute("employees", attendanceService.getAllEmployees());
        model.addAttribute("statuses", AttendanceStatus.values());
        return "attendance/edit";
    }

    // Submit edit form (HR override)
    @PostMapping("/attendance/edit/{id}")
    public String updateAttendance(@PathVariable Long id,
                                   @ModelAttribute Attendance attendance) {
        attendance.setId(id);
        attendanceService.updateAttendance(attendance);
        return "redirect:/attendance";
    }

    // Delete attendance (Admin only)
    @GetMapping("/attendance/delete/{id}")
    public String deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return "redirect:/attendance";
    }

    // Filter by date
    @GetMapping("/attendance/filter")
    public String filterByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        model.addAttribute("attendanceList", attendanceService.getAttendanceByDate(date));
        model.addAttribute("filterDate", date);
        return "attendance/list";
    }

    // ─── EMPLOYEE ─────────────────────────────────────────────

    // Employee views own attendance
    @GetMapping("/employee/attendance")
    public String employeeViewAttendance(Model model, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Employee employee = user.getEmployee();
        model.addAttribute("attendanceList",
                attendanceService.getAttendanceByEmployee(employee.getId()));
        model.addAttribute("employee", employee);
        return "attendance/employee-view";
    }

    // Employee Clock In
    @PostMapping("/employee/clockin")
    public String clockIn(Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        String message = attendanceService.clockIn(user.getEmployee().getId());
        model.addAttribute("message", message);
        model.addAttribute("employee", user.getEmployee());
        return "dashboard/employee";
    }

    // Employee Clock Out
    @PostMapping("/employee/clockout")
    public String clockOut(Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        String message = attendanceService.clockOut(user.getEmployee().getId());
        model.addAttribute("message", message);
        model.addAttribute("employee", user.getEmployee());
        return "dashboard/employee";
    }
}