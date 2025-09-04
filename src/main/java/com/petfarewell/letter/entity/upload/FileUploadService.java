package com.petfarewell.letter.entity.upload;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String upload(MultipartFile file, String folderName);  // 예: "letter"
}