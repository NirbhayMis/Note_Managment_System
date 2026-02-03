package com.example.Notes_Managment_System.Dto;

import com.example.Notes_Managment_System.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoteResponseDto {

    private Long id;
    private String title;
    private String content;
    private Boolean publicNote;
    private Boolean paidNote;
    private Double price;
    private Long uploadedById;
    private Role uploadedByRole;

}
