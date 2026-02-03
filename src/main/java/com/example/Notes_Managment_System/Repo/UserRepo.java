package com.example.Notes_Managment_System.Repo;


import com.example.Notes_Managment_System.Model.Role;
import com.example.Notes_Managment_System.Model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User>findByEmail(String email);
    Optional<User>findByName(String name);
    boolean existsByRole(Role role);
}
