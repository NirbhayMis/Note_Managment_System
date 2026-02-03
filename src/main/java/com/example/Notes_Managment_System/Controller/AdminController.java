package com.example.Notes_Managment_System.Controller;

import com.example.Notes_Managment_System.Dto.ApiResponse;
import com.example.Notes_Managment_System.Dto.NoteRequestDto;
import com.example.Notes_Managment_System.Dto.NoteResponseDto;
import com.example.Notes_Managment_System.Exception.ResourceNotFoundException;
import com.example.Notes_Managment_System.Model.Note;
import com.example.Notes_Managment_System.Model.User;

import com.example.Notes_Managment_System.Repo.NoteRepository;
import com.example.Notes_Managment_System.Repo.UserRepo;
import com.example.Notes_Managment_System.Services.Admin_Services;
import com.example.Notes_Managment_System.Services.User_Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepo userRepository;
    private final Admin_Services adminServices;


    // 🔥 GET ALL USERS
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        return ResponseEntity.ok(
                new ApiResponse<>(200, true,
                        "All users fetched",
                        userRepository.findAll(),null)
        );
    }

    // 🔥 BLOCK USER
    @PutMapping("/block-user/{userId}")
    public ResponseEntity<ApiResponse<?>> blockUser(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "userId","USER_NOT_FOUND",
                        "User not found","Invalid user id"
                ));

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(
                new ApiResponse<>(200,true,"User blocked",null,null)
        );
    }


    // 🔥 UNBLOCK USER
    @PutMapping("/unblock-user/{userId}")
    public ResponseEntity<ApiResponse<?>> unblockUser(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "userId","USER_NOT_FOUND",
                        "User not found","Invalid user id"
                ));

        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok(
                new ApiResponse<>(200,true,"User unblocked",null,null)
        );
    }

    // 🔥 ADMIN UPDATE NOTE
    @PostMapping("/note")
    public ResponseEntity<ApiResponse<NoteResponseDto>> createAdminNote(
            @RequestPart NoteRequestDto request,
            @RequestPart(required = false) MultipartFile file
    ) {

        NoteResponseDto dto =
                adminServices.createOrUpdateAdminNote(request, file);

        return ResponseEntity.ok(
                new ApiResponse<>(200, true,
                        "Admin note created",
                        dto, null)
        );
    }
    @PostMapping("/users/{userId}/note")
    public ResponseEntity<ApiResponse<NoteResponseDto>> createNoteForUser(
            @PathVariable Long userId,
            @RequestPart NoteRequestDto request,
            @RequestPart(required = false) MultipartFile file
    ) {

        NoteResponseDto dto =
                adminServices.adminUpdatedNoteWithFilesUsers(userId, request, file);

        return ResponseEntity.ok(
                new ApiResponse<>(200, true,
                        "Note created by admin for user",
                        dto, null)
        );
    }


    // 🔥 ADMIN DELETE NOTE
    // SOFT DELETE
    @DeleteMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<Void>> adminSoftDelete(@PathVariable Long noteId) {

        adminServices.adminSoftDelete(noteId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        "Note delete to successfully",
                        null,
                        null  // No data returned
                )
        );
    }

    // HARD DELETE (danger)
    @DeleteMapping("/note/{noteId}/hard")
    public ResponseEntity<ApiResponse<Void>> adminHardDelete(
            @PathVariable Long noteId,
            @RequestParam(required = false) String reason) {

        adminServices.adminHardDelete(noteId, reason);

        String message = "Note permanently deleted notification & email send" +
                (reason != null ? ". Reason: " + reason : "");

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        message,
                        null,
                        null  // No data returned
                )
        );
    }


    // 🔥 PUBLIC NOTES
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<NoteResponseDto>>> publicNotes() {

        return ResponseEntity.ok(
                new ApiResponse<>(200,true,
                        "all users notes fetched",

                        adminServices.getPublicNotes(),null)
        );
    }
    @PutMapping("/restore-note/{noteId}")
    public ResponseEntity<ApiResponse<NoteResponseDto>> restoreNote(
            @PathVariable Long noteId
    ) {

        NoteResponseDto dto =
                adminServices.restoreDeletedNote(noteId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        "Note restored successfully",
                        dto,
                        null
                )
        );
    }
//    @PostMapping("/note")
//    public ResponseEntity<ApiResponse<NoteResponseDto>> createAdminNote(
//            @RequestPart NoteRequestDto request,
//            @RequestPart(required = false) MultipartFile file
//    ) {
//
//        NoteResponseDto dto =
//                adminServices.createOrUpdateAdminNote(request, file);
//
//        return ResponseEntity.ok(
//                new ApiResponse<>(200, true,
//                        "Admin note created",
//                        dto,
//                        null)
//        );
//    }
}
