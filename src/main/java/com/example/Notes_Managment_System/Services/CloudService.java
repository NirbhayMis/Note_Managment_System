package com.example.Notes_Managment_System.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
public interface CloudService {
    String uploadFile(MultipartFile file);
    void deleteFile(String fileUrl);

}
