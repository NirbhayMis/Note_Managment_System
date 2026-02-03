package com.example.Notes_Managment_System.Services;

import com.example.Notes_Managment_System.Model.Notification;
import com.example.Notes_Managment_System.Model.User;
import com.example.Notes_Managment_System.Repo.NotificationRepository;
import com.example.Notes_Managment_System.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServices {



        private final NotificationRepository notificationRepository;
        private final UserRepo userRepo;

        public List<Notification> getMyNotifications() {

            String email = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();

            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return notificationRepository.findByUserId(user.getId());
        }
    }


