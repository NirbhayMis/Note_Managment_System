package com.example.Notes_Managment_System.Dto;

import lombok.Data;

@Data
public class VerifyOtpRequestDto {

        private String email;
        private String otp;
        private String newPassword;
    }

