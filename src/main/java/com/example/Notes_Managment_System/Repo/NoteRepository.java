package com.example.Notes_Managment_System.Repo;

import com.example.Notes_Managment_System.Model.Note;
import com.example.Notes_Managment_System.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    // 🔹 EXISTING (as it is)
    List<Note> findByUser(User user);

    List<Note> findByUserId(Long userId);

    List<Note> findByPublicNoteTrue();
    Optional<Note> findByIdAndDeletedTrue(Long id);
    List<Note> findByDeletedTrueAndDeletedAtBefore(LocalDateTime date);


    //  NEW (SOFT DELETE SUPPORT)

    // User ke sirf active notes
    List<Note> findByUserIdAndDeletedFalse(Long userId);

    // Public + not deleted
    List<Note> findByPublicNoteTrueAndDeletedFalse();

    // Single note (safe fetch)
    Optional<Note> findByIdAndDeletedFalse(Long id);
}

