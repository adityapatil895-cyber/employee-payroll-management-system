package com.payroll.payroll.repository;

import com.payroll.payroll.model.Attendance;
import com.payroll.payroll.model.AttendanceStatus;
import com.payroll.payroll.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {


    List<Attendance> findByEmployee(Employee employee);


    List<Attendance> findByDate(LocalDate date);


    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);


    long countByEmployeeAndStatus(Employee employee, AttendanceStatus status);


    List<Attendance> findByEmployeeOrderByDateDesc(Employee employee);

    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);


    List<Attendance> findByEmployeeAndDateBetweenOrderByDateAsc(
            Employee employee, LocalDate start, LocalDate end);


    List<Attendance> findByDateBetweenOrderByDateAsc(
            LocalDate start, LocalDate end);
}