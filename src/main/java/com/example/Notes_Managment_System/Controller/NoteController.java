package com.example.Notes_Managment_System.Controller;
import com.example.Notes_Managment_System.Dto.ApiResponse;
import com.example.Notes_Managment_System.Dto.NoteRequestDto;
import com.example.Notes_Managment_System.Dto.NoteResponseDto;
import com.example.Notes_Managment_System.Model.Note;
import com.example.Notes_Managment_System.Model.Notification;
import com.example.Notes_Managment_System.Model.User;
import com.example.Notes_Managment_System.Repo.UserRepo;
import com.example.Notes_Managment_System.Services.User_Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;


import java.util.List;

@RestController
@RequestMapping("api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final User_Service userService;



    // CREATE NOTE
    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponseDto>> createNote(
            @RequestBody NoteRequestDto request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("AUTH NAME = " + auth.getName());
        System.out.println("AUTHORITIES = " + auth.getAuthorities());

        NoteResponseDto dto = userService.createNote(request);

        ApiResponse<NoteResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                true,
                "Note created successfully",
                dto,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



    // GET MY NOTES
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<NoteResponseDto>>> getMyNotes() {

        List<NoteResponseDto> notes = userService.getMyNotes();

        ApiResponse<List<NoteResponseDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                true,
                "My notes fetched successfully",
                notes,
                null
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<ApiResponse<NoteResponseDto>> update(
            @PathVariable Long noteId,
            @RequestBody NoteRequestDto request) {

        NoteResponseDto dto = userService.updateNote(noteId, request);

        return ResponseEntity.ok(new ApiResponse<>(
                200, true,
                "Note updated successfully",
                dto, null));
    }
    @DeleteMapping("/{noteId}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long noteId) {

        userService.deleteUserNote(noteId);

        return ResponseEntity.ok(new ApiResponse<>(
                200, true,
                "Note deleted successfully",
                null, null));
    }
    @PostMapping("/{noteId}/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @PathVariable Long noteId,
            @RequestParam("file") MultipartFile file) {

        userService.uploadFile(noteId, file);

        return ResponseEntity.ok(new ApiResponse<>(
                200, true,
                "File uploaded successfully",
                null, null));
    }
    @GetMapping("/{noteId}/download")
    public ResponseEntity<?> download(@PathVariable Long noteId) {
        return userService.download(noteId);
    }
}
