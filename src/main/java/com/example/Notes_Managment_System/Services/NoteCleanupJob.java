package com.example.Notes_Managment_System.Services;

import com.example.Notes_Managment_System.Model.Note;
import com.example.Notes_Managment_System.Model.NoteFile;
import com.example.Notes_Managment_System.Repo.NoteFileRepository;
import com.example.Notes_Managment_System.Repo.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteCleanupJob {

    private final NoteRepository noteRepository;
    private final NoteFileRepository noteFileRepository;
    private final CloudService cloudService;

    // 🕑 Runs every day at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteExpiredNotes() {

        LocalDateTime expiryDate = LocalDateTime.now().minusDays(90);

        // 1️⃣ Get expired soft-deleted notes
        List<Note> expiredNotes =
                noteRepository.findByDeletedTrueAndDeletedAtBefore(expiryDate);

        for (Note note : expiredNotes) {

            // 2️⃣ Get all files of this note
            List<NoteFile> files =
                    noteFileRepository.findByNote(note);

            // 3️⃣ Delete files from Cloudinary
            for (NoteFile file : files) {
                if (file.getFileUrl() != null) {
                    cloudService.deleteFile(file.getFileUrl());
                }
            }

            // 4️⃣ Delete file records from DB
            noteFileRepository.deleteAll(files);

            // 5️⃣ Delete note from DB (hard delete)
            noteRepository.delete(note);
        }
    }


}
