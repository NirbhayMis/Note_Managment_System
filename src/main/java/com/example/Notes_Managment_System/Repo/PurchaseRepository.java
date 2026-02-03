package com.example.Notes_Managment_System.Repo;


import com.example.Notes_Managment_System.Model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    boolean existsByUserIdAndNoteId(Long userId, Long noteId);
}
