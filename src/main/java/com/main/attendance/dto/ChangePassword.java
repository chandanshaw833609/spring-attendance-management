package com.main.attendance.dto;

public record ChangePassword(String email, String password, String confirm_password) {
}
