package com.example.Notes_Managment_System.Services;

import com.example.Notes_Managment_System.Model.Role;
import com.example.Notes_Managment_System.Model.User;
import com.example.Notes_Managment_System.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.name}")
    private String adminName;

    @Override
    public void run(String... args) {

        //  Admin already exists → do nothing
        if (userRepo.existsByRole(Role.ADMIN)) {
            log.info("Admin already exists. Skipping admin creation.");
            return;
        }

        // 🔍 Basic safety check
        if (adminEmail == null || adminPassword == null || adminName == null) {
            log.error("Admin credentials missing in application.properties");
            return;
        }

        User admin = new User();
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);

        userRepo.save(admin);

        log.info("Admin user created successfully from application.properties");
    }
}
