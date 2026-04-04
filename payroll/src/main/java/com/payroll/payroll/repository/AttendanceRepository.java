package com.payroll.payroll.repository;

import com.payroll.payroll.model.Attendance;
import com.payroll.payroll.model.AttendanceStatus;
import com.payroll.payroll.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Get all attendance for one employee
    List<Attendance> findByEmployee(Employee employee);

    // Get attendance for a specific date
    List<Attendance> findByDate(LocalDate date);

    // Get attendance for one employee on a specific date
    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);

    // Count absences for payroll deduction
    long countByEmployeeAndStatus(Employee employee, AttendanceStatus status);

    // Get all attendance for one employee ordered by date
    List<Attendance> findByEmployeeOrderByDateDesc(Employee employee);

    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);

    // Monthly attendance for one employee
    List<Attendance> findByEmployeeAndDateBetweenOrderByDateAsc(
            Employee employee, LocalDate start, LocalDate end);

    // Monthly attendance for all employees
    List<Attendance> findByDateBetweenOrderByDateAsc(
            LocalDate start, LocalDate end);
}