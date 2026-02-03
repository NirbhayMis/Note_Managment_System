package com.example.Notes_Managment_System.Dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
    private boolean rememberMe;
}
