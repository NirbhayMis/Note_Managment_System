package com.example.Notes_Managment_System.Services;

import com.example.Notes_Managment_System.Exception.ResourceNotFoundException;
import com.example.Notes_Managment_System.Model.Note;
import com.example.Notes_Managment_System.Model.Purchase;
import com.example.Notes_Managment_System.Model.User;
import com.example.Notes_Managment_System.Repo.NoteRepository;
import com.example.Notes_Managment_System.Repo.PurchaseRepository;
import com.example.Notes_Managment_System.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseServices {
    private final UserRepo userRepo;
    private final NoteRepository noteRepository;
    private  final PurchaseRepository purchaseRepository;
    public void purchaseNote(Long noteId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USER_404",
                        "User not found",
                        "email",
                        "User does not exist"
                ));

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NOTE_404",
                        "Note not found",
                        "noteId",
                        "Note does not exist"
                ));

        //  free note pe payment allow mat karo
        if (!note.getPaidNote()) {
            throw new IllegalStateException("This note is free");
        }

        //  double purchase prevent
        boolean alreadyPurchased =
                purchaseRepository.existsByUserIdAndNoteId(user.getId(), noteId);

        if (alreadyPurchased) {
            throw new IllegalStateException("Note already purchased");
        }

        Purchase purchase = new Purchase();
        purchase.setUserId(user.getId());
        purchase.setNoteId(noteId);
        purchase.setAmount(note.getPrice());
        purchase.setPaid(true);

        purchaseRepository.save(purchase);
    }

}
