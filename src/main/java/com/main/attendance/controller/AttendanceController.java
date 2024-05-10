package com.main.attendance.controller;

import com.main.attendance.entity.Attendance;
import com.main.attendance.entity.Employee;
import com.main.attendance.repository.AttendanceRepository;
import com.main.attendance.repository.EmployeeRepository;
import com.main.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.DateFormatter;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

@RestController
@CrossOrigin({"http://localhost:5173", "https://vimal-sakseria.vercel.app"})
public class AttendanceController {

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @PostMapping("/attendance/{location}")
    public ResponseEntity<?> giveAttendance(Principal principal, @PathVariable String location) {
        System.out.println("My location is : " + location);
        if (LocalTime.now().isAfter(LocalTime.of(16,0))) {
            return ResponseEntity.badRequest().body("You Add Your Attendance now!");
        }
        Employee employee = employeeRepository.findByEmail(principal.getName()).get();
        Attendance attendance = new Attendance(employee, location);
        attendanceRepository.save(attendance);
        return ResponseEntity.ok("Employee checked in successfully");
    }


    @GetMapping("/attendance/employee")
    public ResponseEntity<?> getAttendanceByEmployee(Principal principal) {
        Employee employee = employeeRepository.findByEmail(principal.getName()).get();
        List<Attendance> attendanceList = attendanceRepository.findByEmployee(employee);
        return ResponseEntity.ok(attendanceList);
    }

    @GetMapping("/attendance/today")
    public ResponseEntity<?> getTodayAttendance(Principal principal) {
        Employee employee = employeeRepository.findByEmail(principal.getName()).get();
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee,LocalDate.now());
        return ResponseEntity.ok(attendance);
    }
    @GetMapping("/attendance")
    public ResponseEntity<?> getAllAttendance() {
        return ResponseEntity.ok(attendanceRepository.findAll());
    }

    @GetMapping("/attendance/{date}")
    public ResponseEntity<?> getAttendanceByDate(@PathVariable String date) {
        System.out.println("My date is : " + date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyyy");

        LocalDate localDate = LocalDate.parse(date, formatter);
        List<Attendance> attendanceList = attendanceRepository.findByDate(localDate);
        System.out.println(attendanceList.toString());
        return ResponseEntity.ok(attendanceRepository.findByDate(localDate));
    }

    @GetMapping("/custom-attendance/{startingDate}/{finalDate}")
    public ResponseEntity<?> getCustomAttendance(Principal principal,@PathVariable String startingDate, @PathVariable String finalDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startingDate, formatter);
        LocalDate endDate = LocalDate.parse(finalDate, formatter);
        Employee employee = employeeRepository.findByEmail(principal.getName()).get();
        return ResponseEntity.ok(attendanceRepository.findByEmployeeAndDateBetween(employee,startDate, endDate));
    }

    @GetMapping("/custom-attendance/{employeeId}/{startingDate}/{finalDate}")
    public ResponseEntity<?> getCustomAttendance(@PathVariable Long employeeId,@PathVariable String startingDate, @PathVariable String finalDate) {
        System.out.println("My employee id is : " + employeeId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startingDate, formatter);
        LocalDate endDate = LocalDate.parse(finalDate, formatter);
        Employee employee = employeeRepository.findById(employeeId).get();
        return ResponseEntity.ok(attendanceRepository.findByEmployeeAndDateBetween(employee,startDate, endDate));
    }
}
