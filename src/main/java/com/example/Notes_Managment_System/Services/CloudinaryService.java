package com.example.Notes_Managment_System.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService implements CloudService {

    @Override
    public String uploadFile(MultipartFile file) {
        return "https://dummy.cloudinary.com/uploaded-file.pdf";
    }

    @Override
    public void deleteFile(String fileUrl){
        System.out.println("Deleting from Cloudinary: " + fileUrl);
    }
}
