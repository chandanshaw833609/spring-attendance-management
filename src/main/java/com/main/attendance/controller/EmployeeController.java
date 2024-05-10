package com.main.attendance.controller;

import com.main.attendance.entity.Employee;
import com.main.attendance.repository.AttendanceRepository;
import com.main.attendance.repository.EmployeeRepository;
import com.main.attendance.service.AttendanceService;
import com.main.attendance.service.EmployeeService;
import com.main.attendance.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin({"http://localhost:5173", "https://vimal-sakseria.vercel.app"})
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody Employee employee) {
        Optional<Employee> emp = employeeRepository.findByEmail(employee.getEmail());
        if (emp.isPresent()) return ResponseEntity.badRequest().body("Email already exist");
        employeeService.addEmployee(employee);
        return ResponseEntity.ok("Employee added successfully.");
    }

    @GetMapping("/employee")
    public ResponseEntity<?> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @GetMapping("/hello")
    public ResponseEntity<?> testing() {
        return ResponseEntity.ok("Hello World!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody Employee employee) {
        Optional<Employee> emp = employeeRepository.findByEmail(employee.getEmail());
        if (emp.isEmpty()) return ResponseEntity.badRequest().body("User does not exist");

        if (!emp.get().isVerified()) {
            return ResponseEntity.badRequest().body("Your account is not verified");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(employee.getEmail(), employee.getPassword()));
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(emp.get().getEmail(), authentication.getAuthorities());
            return ResponseEntity.ok( Map.of("token", token));
        } else {
            return  ResponseEntity.badRequest().body("invalid details");
        }
    }

    @GetMapping("/employee/unverified")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getUnverifiedEmployee() {
        return ResponseEntity.ok(employeeRepository.findByIsVerified(false));
    }

    @PutMapping("/employee/verify/{employeeId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> verifyAccount(@PathVariable Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).get();
        employee.setVerified(true);
        employeeRepository.save(employee);
        return ResponseEntity.ok("Verified Successfully");
    }

    @DeleteMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long employeeId) {
        employeeRepository.deleteById(employeeId);
        return ResponseEntity.ok("Deleted Successfully");
    }

    @GetMapping("/current-employee")
    public ResponseEntity<?> getCurrentEmployee(Principal principal) {
        Employee employee = employeeRepository.findByEmail(principal.getName()).get();
        return ResponseEntity.ok(employee);
    }


//    public ResponseEntity<?> singIn(@RequestBody Employee req) {
//        String email = req.getEmail();
//        Employee employee = employeeService.signInEmployee(email, req.getPassword());
//        return ResponseEntity.ok(employee);
//    }
}
