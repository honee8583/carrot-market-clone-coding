package com.carrot.carrotmarketclonecoding.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadImage(MultipartFile file);
    void deleteUploadedImage(String url);
}
