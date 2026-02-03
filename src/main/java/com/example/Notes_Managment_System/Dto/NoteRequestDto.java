package com.example.Notes_Managment_System.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class NoteRequestDto {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Content is required")
    private String content;
    private Boolean publicNote;
    private Boolean paidNote;
    private Double price;
}
