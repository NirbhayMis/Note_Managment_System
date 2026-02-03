package com.example.Notes_Managment_System.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRSponse {
    private String token;
    private String role;
}
