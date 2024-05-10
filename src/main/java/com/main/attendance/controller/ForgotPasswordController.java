package com.main.attendance.controller;

import com.main.attendance.dto.ChangePassword;
import com.main.attendance.dto.MailBody;
import com.main.attendance.entity.Employee;
import com.main.attendance.entity.ForgotPassword;
import com.main.attendance.repository.EmployeeRepository;
import com.main.attendance.repository.ForgotPasswordRepository;
import com.main.attendance.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/forgot-password")
@CrossOrigin("http://localhost:5173")
public class ForgotPasswordController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/generateOtp/{email}")
    public ResponseEntity<?> verifyEmail(@PathVariable String email) {
        System.out.println("my email is : " + email);
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isEmpty()) return ResponseEntity.badRequest().body("User does not exist");
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is mail is send to you for your forgot password request, You can verify your account with this otp")
                .subject("OTP for forgot password request: " + otp)
                .build();

        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setOtp(otp);
        forgotPassword.setEmployee(employee.get());
        forgotPassword.setExpirationTime(LocalTime.now().plusMinutes(10));

        ForgotPassword forgotPassword1 = forgotPasswordRepository.findByEmployee(employee.get());
        if (forgotPassword1 != null) {
            forgotPasswordRepository.delete(forgotPassword1);
        }
        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("Email send for verification");
    }


    @PostMapping("/verifyOtp/{email}/{otp}")
    public ResponseEntity<?> verifyOtp(@PathVariable String email, @PathVariable int otp) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() ->new UsernameNotFoundException("User not found"));
        System.out.println("My employee is : " + employee.getName());
        System.out.println("My otp is : " + otp);
        Optional<ForgotPassword> forgotPasswordOptional = forgotPasswordRepository.findByEmployeeAndOtp(employee, otp);
        if (forgotPasswordOptional.isEmpty()) return ResponseEntity.badRequest().body("OTP is invalid");
        ForgotPassword forgotPassword = forgotPasswordOptional.get();
        if (forgotPassword.getExpirationTime().isBefore(LocalTime.now())) {
                forgotPasswordRepository.delete(forgotPassword);
               return new  ResponseEntity<>("OTP is expired", HttpStatus.EXPECTATION_FAILED);
        }
        forgotPassword.setOtpVerified(true);
        forgotPasswordRepository.save(forgotPassword);
        return ResponseEntity.ok("OTP is verified");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> resetPassword(@RequestBody ChangePassword changePassword) {
        Employee employee = employeeRepository.findByEmail(changePassword.email())
                .orElseThrow(() ->new UsernameNotFoundException("User not found"));
        System.out.println(employee.getName() + "-> This is my employee");
        ForgotPassword forgotPassword = forgotPasswordRepository.findByEmployee(employee);
        if (forgotPassword == null || !forgotPassword.isOtpVerified()) return ResponseEntity.badRequest().body("Please verify your otp");

        forgotPasswordRepository.delete(forgotPassword);
        employee.setPassword(passwordEncoder.encode(changePassword.password()));
        employeeRepository.save(employee);
        return ResponseEntity.ok("Password is changed Successfully");
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000,999_999);
    }
}
