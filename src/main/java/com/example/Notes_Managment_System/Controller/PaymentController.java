package com.example.Notes_Managment_System.Controller;

import com.example.Notes_Managment_System.Dto.ApiResponse;
import com.example.Notes_Managment_System.Services.PurchaseServices;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PurchaseServices purchaseServices; // ✅ fixed variable name

    @PostMapping("/purchase/{noteId}")
    public ResponseEntity<ApiResponse<String>> purchaseNote(
            @PathVariable Long noteId) {

        purchaseServices.purchaseNote(noteId); // ✅ fixed method call

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        "Payment successful. Note unlocked.",
                        null,
                        null
                )
        );
    }
}