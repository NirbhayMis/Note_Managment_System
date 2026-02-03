package com.example.Notes_Managment_System.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDetails {
private String field;
    private String code;
    private String description;

}
