package com.example.Notes_Managment_System.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PasswordResetOtp {

    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private String otp;
    private LocalDateTime expiryTime;
}

