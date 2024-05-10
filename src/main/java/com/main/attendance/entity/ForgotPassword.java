package com.main.attendance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@AllArgsConstructor
@Data
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer otp;
    @Column(nullable = false)
    private LocalTime expirationTime;
    private boolean isOtpVerified;
    @OneToOne
    private Employee employee;

    public ForgotPassword() {
        this.isOtpVerified = false;
    }
}
