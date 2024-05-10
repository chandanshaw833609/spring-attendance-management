package com.main.attendance.repository;

import com.main.attendance.entity.Employee;
import com.main.attendance.entity.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    Optional<ForgotPassword> findByEmployeeAndOtp(Employee employee, int otp);

    ForgotPassword findByEmployee(Employee employee);
}
