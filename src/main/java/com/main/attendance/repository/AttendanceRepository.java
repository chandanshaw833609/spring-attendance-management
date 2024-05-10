package com.main.attendance.repository;

import com.main.attendance.entity.Attendance;
import com.main.attendance.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Attendance findByEmployeeAndDate(Employee employee, LocalDate now);

    List<Attendance> findByEmployee(Employee employee);

    List<Attendance> findByDate(LocalDate localDate);

    List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate start, LocalDate end);

}
