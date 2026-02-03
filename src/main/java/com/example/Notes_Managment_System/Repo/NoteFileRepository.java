package com.example.Notes_Managment_System.Repo;

import com.example.Notes_Managment_System.Model.Note;
import com.example.Notes_Managment_System.Model.NoteFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteFileRepository extends JpaRepository<NoteFile, Long> {

    // 🔥 kisi note ke saare files
    List<NoteFile> findByNote(Note note);

    // 🔥 noteId se files
    List<NoteFile> findByNote_Id(Long noteId);

    // 🔥 delete all files of a note (DB only)
    void deleteByNote(Note note);
}
