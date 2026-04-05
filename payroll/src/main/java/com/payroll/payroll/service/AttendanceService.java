package com.payroll.payroll.service;

import com.payroll.payroll.model.*;
import com.payroll.payroll.repository.AttendanceRepository;
import com.payroll.payroll.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // HR marks attendance manually
    public String markAttendance(Attendance attendance) {

        if (attendance.getEmployee() == null || attendance.getStatus() == null) {
            return "Please fill all required fields!";
        }

        Optional<Attendance> existing = attendanceRepository
                .findByEmployeeAndDate(attendance.getEmployee(), attendance.getDate());

        if (existing.isPresent()) {
            return "Attendance already exists for this date!";
        }

        attendance.setDate(LocalDate.now());

        attendanceRepository.save(attendance);
        return "Attendance marked successfully!";
    }

    // Employee clocks in
    public String clockIn(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        Optional<Attendance> existing = attendanceRepository
                .findByEmployeeAndDate(employee, LocalDate.now());
        if (existing.isPresent()) {
            return "Already clocked in today!";
        }
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(LocalDate.now());
        attendance.setClockIn(LocalTime.now());
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendanceRepository.save(attendance);
        return "Clock In successful at " + LocalTime.now().withNano(0);
    }

    // Employee clocks out
    public String clockOut(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        Optional<Attendance> existing = attendanceRepository
                .findByEmployeeAndDate(employee, LocalDate.now());
        if (existing.isEmpty()) {
            return "You have not clocked in today!";
        }
        Attendance attendance = existing.get();
        if (attendance.getClockOut() != null) {
            return "Already clocked out today!";
        }
        attendance.setClockOut(LocalTime.now());
        attendanceRepository.save(attendance);
        return "Clock Out successful at " + LocalTime.now().withNano(0);
    }

    // Get all attendance records
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    // Get attendance by date
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    // Get attendance by employee (all records)
    public List<Attendance> getAttendanceByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return attendanceRepository.findByEmployeeOrderByDateDesc(employee);
    }

    // Get attendance by ID
    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id).orElseThrow();
    }

    // Update attendance
    public void updateAttendance(Attendance attendance) {
        attendanceRepository.save(attendance);
    }

    // Delete attendance
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    // Count absences for payroll
    public long countAbsences(Employee employee) {
        return attendanceRepository
                .countByEmployeeAndStatus(employee, AttendanceStatus.ABSENT);
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // ✅ FIXED: Get last 2 months attendance for ONE employee
    public List<Attendance> getMonthlyAttendance(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();

        LocalDate start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate end = LocalDate.now();

        return attendanceRepository
                .findByEmployeeAndDateBetweenOrderByDateAsc(employee, start, end);
    }

    // ✅ FIXED: Get last 2 months attendance for ALL employees (HR)
    public List<Attendance> getAllMonthlyAttendance() {

        LocalDate start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate end = LocalDate.now();

        return attendanceRepository
                .findByDateBetweenOrderByDateAsc(start, end);
    }
}