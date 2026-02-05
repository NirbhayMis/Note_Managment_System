package com.example.Notes_Managment_System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NotesManagmentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotesManagmentSystemApplication.class, args);
	}

}
