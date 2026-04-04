package com.payroll.payroll.controller;

import com.payroll.payroll.model.*;
import com.payroll.payroll.repository.UserRepository;
import com.payroll.payroll.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserRepository userRepository;

    // ─── ADMIN & HR ───────────────────────────────────────────

    // View all attendance
    // View all attendance - show employee list
    @GetMapping("/attendance")
    public String viewAll(Model model) {
        model.addAttribute("employees", attendanceService.getAllEmployees());
        return "attendance/list";
    }

    // Show mark attendance form
    @GetMapping("/attendance/mark")
    public String showMarkForm(Model model) {
        model.addAttribute("attendance", new Attendance());
        model.addAttribute("employees", attendanceService.getAllEmployees());
        model.addAttribute("statuses", AttendanceStatus.values());
        model.addAttribute("today", LocalDate.now());
        return "attendance/mark";
    }

    // Submit mark attendance
    @PostMapping("/attendance/mark")
    public String markAttendance(@ModelAttribute Attendance attendance) {
        attendanceService.markAttendance(attendance);
        return "redirect:/attendance";
    }

    // Show edit form
    @GetMapping("/attendance/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("attendance", attendanceService.getAttendanceById(id));
        model.addAttribute("employees", attendanceService.getAllEmployees());
        model.addAttribute("statuses", AttendanceStatus.values());
        return "attendance/edit";
    }

    // Submit edit form
    @PostMapping("/attendance/edit/{id}")
    public String updateAttendance(@PathVariable Long id,
                                   @ModelAttribute Attendance attendance) {
        attendance.setId(id);
        attendanceService.updateAttendance(attendance);
        return "redirect:/attendance";
    }

    // Delete attendance
    @GetMapping("/attendance/delete/{id}")
    public String deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return "redirect:/attendance";
    }

    // Filter by date
    @GetMapping("/attendance/filter")
    public String filterByDate(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        if (date == null) {
            return "redirect:/attendance";
        }
        model.addAttribute("attendanceList", attendanceService.getAttendanceByDate(date));
        model.addAttribute("filterDate", date);
        return "attendance/list";
    }

    // HR views monthly attendance of ALL employees
    @GetMapping("/hr/attendance/monthly")
    public String hrMonthlyAttendance(Model model) {
        model.addAttribute("attendanceList", attendanceService.getAllMonthlyAttendance());
        model.addAttribute("currentMonth", LocalDate.now().getMonth().toString());
        model.addAttribute("employees", attendanceService.getAllEmployees());
        return "attendance/hr-monthly";
    }

    // ─── EMPLOYEE ─────────────────────────────────────────────

    // Employee views OWN monthly attendance
    @GetMapping("/employee/attendance")
    public String employeeViewAttendance(Model model, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Employee employee = user.getEmployee();

        if (employee == null) {
            model.addAttribute("attendanceList", Collections.emptyList());
            model.addAttribute("employee", null);
            model.addAttribute("errorMessage", "Your account is not linked to an employee profile yet.");
            return "attendance/employee-view";
        }

        model.addAttribute("attendanceList",
                attendanceService.getMonthlyAttendance(employee.getId()));
        model.addAttribute("employee", employee);
        model.addAttribute("currentMonth", LocalDate.now().getMonth().toString());
        return "attendance/employee-view";
    }

    // Employee Clock In
    @PostMapping("/employee/clockin")
    public String clockIn(Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (user.getEmployee() == null) {
            model.addAttribute("message", "Your account is not linked to an employee profile yet.");
            return "dashboard/employee";
        }
        String message = attendanceService.clockIn(user.getEmployee().getId());
        model.addAttribute("message", message);
        model.addAttribute("employee", user.getEmployee());
        return "dashboard/employee";
    }

    // Employee Clock Out
    @PostMapping("/employee/clockout")
    public String clockOut(Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (user.getEmployee() == null) {
            model.addAttribute("message", "Your account is not linked to an employee profile yet.");
            return "dashboard/employee";
        }
        String message = attendanceService.clockOut(user.getEmployee().getId());
        model.addAttribute("message", message);
        model.addAttribute("employee", user.getEmployee());
        return "dashboard/employee";
    }

    // HR views ONE employee monthly attendance
    // HR views ONE employee monthly attendance
    @GetMapping("/hr/attendance/employee/{id}")
    public String hrViewEmployeeAttendance(@PathVariable Long id, Model model) {
        List<Attendance> list = attendanceService.getMonthlyAttendance(id);
        model.addAttribute("attendanceList", list);
        model.addAttribute("currentMonth", LocalDate.now().getMonth().toString());
        return "attendance/hr-monthly";
    }
}