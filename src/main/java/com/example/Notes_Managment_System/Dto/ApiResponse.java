package com.example.Notes_Managment_System.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private int status;
    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;


}
