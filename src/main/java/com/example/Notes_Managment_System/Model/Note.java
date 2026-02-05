package com.example.Notes_Managment_System.Model;

import com.example.Notes_Managment_System.Model.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "notes")
 public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    @Column(nullable = false)
    private Boolean deleted = false;
   @ManyToOne
   @JoinColumn(name = "user_id")
   private User user;
    @Column(nullable = false)
    private Boolean publicNote;

    private Boolean paidNote;
    private double price;
    private LocalDateTime deletedAt;
    @Enumerated(EnumType.STRING)
    private Role uploadedByRole; // USER / ADMIN
    private Long uploadedById;
    private LocalDateTime uploatedDateTime;
    private LocalDateTime updatedDateTime;
    @OneToMany(
            mappedBy = "note",
            cascade =  CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NoteFile> files = new ArrayList<>();

}
