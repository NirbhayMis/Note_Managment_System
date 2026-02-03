package com.example.Notes_Managment_System.Dto;

import com.example.Notes_Managment_System.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReigsterResponseDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
