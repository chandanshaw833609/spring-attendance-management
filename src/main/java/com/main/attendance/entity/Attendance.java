package com.main.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Employee employee;
    private String location;
    private boolean isPresent;
    private LocalDate date;

    public Attendance(Employee employee, String location) {
        this.isPresent = true;
        this.date = LocalDate.now();
        this.employee = employee;
        this.location = location;
    }
}
