package com.example.Notes_Managment_System.Controller;

import com.example.Notes_Managment_System.Model.Notification;
import com.example.Notes_Managment_System.Services.NotificationServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationServices notificationService;

    @GetMapping("/my")
    public ResponseEntity<List<Notification>> myNotifications() {

        return ResponseEntity.ok(
                notificationService.getMyNotifications()
        );
    }
}
