package com.example.Notes_Managment_System.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "notesFile")
public class NoteFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileUrl;
    private String fileType;
    private String folder;

    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "note_id")
    private Note note;
}
