package com.example.Notes_Managment_System.Services;

import com.example.Notes_Managment_System.Dto.NoteRequestDto;
import com.example.Notes_Managment_System.Dto.NoteResponseDto;
import com.example.Notes_Managment_System.Exception.ResourceNotFoundException;
import com.example.Notes_Managment_System.Model.*;
import com.example.Notes_Managment_System.Repo.NoteRepository;
import com.example.Notes_Managment_System.Repo.NotificationRepository;
import com.example.Notes_Managment_System.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.Notes_Managment_System.Model.Role.USER;


@Service
@RequiredArgsConstructor
@Transactional
public class Admin_Services {

    private final NoteRepository noteRepository;
    private final UserRepo userRepo;
    private final CloudService cloudService;
    private final NotificationRepository notificationRepository;
    private  final EmailServices emailServices;

    // 🔐 helper: logged-in admin
    private User getLoggedInAdmin() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can perform this action");
        }
        return user;
    }

     //AdminOwn Note Created
     @Transactional
     public NoteResponseDto createOrUpdateAdminNote(
             NoteRequestDto request,
             MultipartFile file
     ) {

         User admin = getLoggedInAdmin();

         Note note = new Note();
         note.setTitle(request.getTitle());
         note.setContent(request.getContent());
         note.setPublicNote(
                 request.getPublicNote() != null ? request.getPublicNote() : true
         );
         note.setPaidNote(
                 request.getPaidNote() != null ? request.getPaidNote() : false
         );
         note.setPrice(
                 request.getPrice() != null ? request.getPrice() : 0.0
         );

         note.setUser(admin);
         note.setUploadedByRole(Role.ADMIN);
         note.setUploadedById(admin.getId());
         note.setDeleted(false);

         Note savedNote = noteRepository.save(note);

         // 🔥 SINGLE FILE OPTIONAL
         if (file != null && !file.isEmpty()) {

             String url = cloudService.uploadFile(file);

             NoteFile noteFile = new NoteFile();
             noteFile.setFileUrl(url);
             noteFile.setFileType(file.getContentType());
             noteFile.setUploadedAt(LocalDateTime.now());
             noteFile.setNote(savedNote);

             savedNote.getFiles().add(noteFile);
         }

         return mapToDto(savedNote);
     }




    // ✅ ADMIN UPDATE NOTE (PATCH SAFE)
    @Transactional
    public NoteResponseDto adminUpdatedNoteWithFilesUsers(
            Long userId,
            NoteRequestDto request,
            MultipartFile file   // ✅ SINGLE FILE
    ) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "userId",
                        "USER_NOT_FOUND",
                        "User not found",
                        "Invalid user id"
                ));

        User admin = getLoggedInAdmin();

        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setPublicNote(
                request.getPublicNote() != null ? request.getPublicNote() : true
        );
        note.setPaidNote(
                request.getPaidNote() != null ? request.getPaidNote() : false
        );
        note.setPrice(
                request.getPrice() != null ? request.getPrice() : 0.0
        );

        note.setUser(user);
        note.setUploadedById(admin.getId());
        note.setUploadedByRole(Role.ADMIN);
        note.setDeleted(false);

        Note savedNote = noteRepository.save(note);

        // 🔥 FILE UPLOAD (OPTIONAL, SINGLE FILE)
        if (file != null && !file.isEmpty()) {

            String fileUrl = cloudService.uploadFile(file);

            NoteFile noteFile = new NoteFile();
            noteFile.setFileUrl(fileUrl);
            noteFile.setFileType(file.getContentType());
            noteFile.setUploadedAt(LocalDateTime.now());
            noteFile.setNote(savedNote);

            savedNote.getFiles().add(noteFile);
        }

        return mapToDto(savedNote);
    }

    // ✅ ADMIN DELETE NOTE (SOFT DELETE)
    @Transactional
    public void adminSoftDelete(Long noteId) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setDeleted(true);


        noteRepository.save(note);
    }
//Admin hard Deleted
    @Transactional
    public void adminHardDelete(Long noteId, String reason) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // ☁️ delete all cloud files
        for (NoteFile file : note.getFiles()) {
            cloudService.deleteFile(file.getFileUrl());
        }

        User owner = note.getUser();

        noteRepository.delete(note);

        Notification notification = new Notification();
        notification.setUserId(owner.getId());
        notification.setMessage(
                "Your note '" + note.getTitle() +
                        "' was deleted by Admin. Reason: " + reason
        );

        notificationRepository.save(notification);

        emailServices.sendEmail(
                owner.getEmail(),
                "Your note was deleted by Admin",
                notification.getMessage()
        );
    }



    // ✅ GET PUBLIC NOTES (non-deleted)
    @Transactional(readOnly = true)
    public List<NoteResponseDto> getPublicNotes() {

        return noteRepository
                .findByPublicNoteTrueAndDeletedFalse()
                .stream()
                .map(this::mapToDto)
                .toList();
    }



    private NoteResponseDto mapToDto(Note note) {
        return new NoteResponseDto(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getPublicNote(),
                note.getPaidNote(),
                note.getPrice(),
                note.getUploadedById(),
                note.getUploadedByRole()
        );
    }

    @Transactional
    public NoteResponseDto restoreDeletedNote(Long noteId) {

        Note note = noteRepository.findByIdAndDeletedTrue(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "noteId",
                        "NOTE_NOT_FOUND",
                        "Note not found",
                        "Deleted note with ID " + noteId + " does not exist"
                ));

        // 🔁 RESTORE
        note.setDeleted(false);

        Note restored = noteRepository.save(note);

        return new NoteResponseDto(
                restored.getId(),
                restored.getTitle(),
                restored.getContent(),
                restored.getPublicNote(),
                restored.getPaidNote(),
                restored.getPrice(),
                restored.getUploadedById(),
                restored.getUploadedByRole()
        );
    }


}