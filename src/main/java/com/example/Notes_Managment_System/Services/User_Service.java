package com.example.Notes_Managment_System.Services;

import com.example.Notes_Managment_System.Dto.*;
import com.example.Notes_Managment_System.Exception.*;
import com.example.Notes_Managment_System.Model.*;
import com.example.Notes_Managment_System.Repo.*;
import com.example.Notes_Managment_System.SecurityConfig.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class User_Service {

    /* ================= DEPENDENCIES ================= */

    private final UserRepo userRepo;
    private final NoteRepository noteRepository;
    private final PurchaseRepository purchaseRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;

    private final JwtUtil jwtUtil;
    private final CloudService cloudService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailServices emailServices;

    private  final NoteFileRepository noteFileRepository;
    /* =================================================
                       AUTH SECTION
       ================================================= */

    public ReigsterResponseDto register(RegisterRequestDto request) {

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new Duplicated(
                    "Email already exists",
                    "DUPLICATE_EMAIL",
                    "email",
                    "Email already registered"
            );
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User saved = userRepo.save(user);

        return new ReigsterResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole()
        );
    }

    public LoginRSponse login(LoginRequestDto request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCreditionalException("Invalid Email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCreditionalException("Invalid Password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginRSponse(token, user.getRole().name());
    }

    /* =================================================
                       NOTE SECTION
       ================================================= */

    public NoteResponseDto createNote(NoteRequestDto request) {

        User user = getLoggedInUser();

        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setPublicNote(request.getPublicNote());
        note.setPaidNote(request.getPaidNote());
        note.setPrice(request.getPrice() != null ? request.getPrice() : 0);

        note.setUser(user);
        note.setUploadedById(user.getId());
        note.setUploadedByRole(user.getRole());
        note.setUploatedDateTime(LocalDateTime.now());

        return mapToDto(noteRepository.save(note));
    }

    public List<NoteResponseDto> getMyNotes() {

        User user = getLoggedInUser();

        return noteRepository.findByUserIdAndDeletedFalse(user.getId())
                .stream()
                .map(note -> {
                    NoteResponseDto dto = mapToDto(note);

                    // Hide content if paid & not purchased
                    if (Boolean.TRUE.equals(note.getPaidNote())) {
                        boolean purchased =
                                purchaseRepository.existsByUserIdAndNoteId(
                                        user.getId(), note.getId()
                                );
                        if (!purchased) {
                            dto.setContent("🔒 Paid note. Purchase required.");
                        }
                    }
                    return dto;
                })
                .toList();
    }

    public NoteResponseDto updateNote(Long noteId, NoteRequestDto request) {

        User user = getLoggedInUser();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOTE_404", "Note not found", "noteId", "Invalid note ID"
                ));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new ValidationException(
                    "NOTE_403",
                    "You cannot update this note",
                    "noteId",
                    "You are not the owner of this note"
            );
        }

        if (request.getTitle() != null) note.setTitle(request.getTitle());
        if (request.getContent() != null) note.setContent(request.getContent());
        if (request.getPublicNote() != null) note.setPublicNote(request.getPublicNote());

        if (request.getPaidNote() != null) {
            note.setPaidNote(request.getPaidNote());
            note.setPrice(request.getPaidNote()
                    ? request.getPrice()
                    : 0
            );
            note.setUpdatedDateTime(LocalDateTime.now());
        }

        return mapToDto(noteRepository.save(note));
    }

    @Transactional
    public void deleteUserNote(Long noteId) {

        User user = getLoggedInUser();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOTE_404", "Note not found", "noteId", "Invalid note ID"
                ));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new ValidationException("NOTE_403",
                    "You cannot update this note",
                    "noteId",
                    "Unauthorized note access");
        }

        if (note.getUploadedByRole() == Role.ADMIN) {
            throw new ResourceNotFoundException("NOTE_403", "You cannot delete this note", "noteId", "You are not the owner");
        }

        note.setDeleted(true);
        note.setDeletedAt(LocalDateTime.now());
        noteRepository.save(note);
    }

    /* =================================================
                       FILE SECTION
       ================================================= */

    @Transactional
    public void uploadFile(Long noteId, MultipartFile file) {

        User user = getLoggedInUser();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "noteId",
                        "NOTE_NOT_FOUND",
                        "Note not found",
                        "Invalid note ID"
                ));

        // 🔐 Ownership check
        if (!note.getUser().getId().equals(user.getId())) {
            throw new ValidationException(
                    "NOTE_403",
                    "You cannot upload file to this note",
                    "noteId",
                    "Unauthorized note access"
            );
        }

        // 📎 File validation
        if (file == null || file.isEmpty()) {
            throw new ValidationException(
                    "FILE_400",
                    "File is empty",
                    "file",
                    "Please upload a valid file"
            );
        }

        // ☁️ Upload to cloud
        String url = cloudService.uploadFile(file);

        // 🧾 Save file metadata (NO overwrite)
        NoteFile noteFile = new NoteFile();
        noteFile.setFileUrl(url);
        noteFile.setFileType(file.getContentType());
        noteFile.setUploadedAt(LocalDateTime.now());
        noteFile.setNote(note);

        note.getFiles().add(noteFile);

        noteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> download(Long noteId) {

        User user = getLoggedInUser();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "noteId",
                        "NOTE_NOT_FOUND",
                        "Note not found",
                        "Invalid note id"
                ));

        // 🔐 PAID NOTE CHECK
        if (Boolean.TRUE.equals(note.getPaidNote())) {
            boolean purchased =
                    purchaseRepository.existsByUserIdAndNoteId(
                            user.getId(), noteId
                    );

            if (!purchased) {
                throw new AccessDeniedException("You have not purchased this note");
            }
        }

        // 📎 FILE EXIST CHECK
        if (note.getFiles() == null || note.getFiles().isEmpty()) {
            throw new ResourceNotFoundException(
                    "FILE_404",
                    "No file available for this note",
                    "noteId",
                    "File not uploaded yet"
            );
        }

        // 🔥 GET LATEST FILE
        NoteFile latestFile = note.getFiles()
                .stream()
                .max((f1, f2) ->
                        f1.getUploadedAt().compareTo(f2.getUploadedAt()))
                .orElseThrow(() ->
                        new RuntimeException("File not found")
                );

        // 🔁 REDIRECT TO CLOUD URL
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", latestFile.getFileUrl())
                .build();
    }


    /* =================================================
                   PASSWORD RESET SECTION
       ================================================= */

    public void forgotPassword(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USER_404", "User not found", "email", email
                ));

        passwordResetOtpRepository.deleteByEmail(email);

        String otp = String.valueOf(
                100000 + new Random().nextInt(900000)
        );

        PasswordResetOtp resetOtp = new PasswordResetOtp();
        resetOtp.setEmail(email);
        resetOtp.setOtp(otp);
        resetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        passwordResetOtpRepository.save(resetOtp);

        emailServices.sendEmail(
                email,
                "Password Reset OTP",
                "Your OTP is: " + otp + "\nValid for 5 minutes."
        );
    }

    public void resetPassword(VerifyOtpRequestDto request) {

        PasswordResetOtp resetOtp =
                passwordResetOtpRepository
                        .findByEmailAndOtp(
                                request.getEmail(),
                                request.getOtp()
                        )
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "OTP_404", "Invalid OTP", "otp", request.getOtp()
                        ));

        if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepo.save(user);
        passwordResetOtpRepository.deleteByEmail(request.getEmail());
    }

    /* =================================================
                       HELPERS
       ================================================= */

    private User getLoggedInUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
}
