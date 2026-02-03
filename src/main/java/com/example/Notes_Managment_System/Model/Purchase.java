package com.example.Notes_Managment_System.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long noteId;

    private double amount;

    private boolean paid;
}
