package com.main.attendance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private boolean isVerified;
    @OneToOne(mappedBy = "employee")
    @JsonIgnore
    private ForgotPassword forgotPassword;
    private String roles;

    public Employee() {
        this.roles = "ROLE_USER";
    }
//    @OneToMany(mappedBy = "employee")
//    @JoinColumn(name = "employee_id", referencedColumnName = "id")
//    private List<Attendance> attendanceList;
}
