package com.example.imagemanagementmicroservice.controller;

import com.example.imagemanagementmicroservice.dto.ErrorResponse;
import com.example.imagemanagementmicroservice.dto.ImageUploadResponse;
import com.example.imagemanagementmicroservice.service.MinIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/images")
@CrossOrigin
public class ImageControllerV1 {

    @Autowired
    private MinIOService minIOService;

    @PostMapping()
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.builder()
                                .success(false)
                                .message("File is empty")
                                .build());
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.builder()
                                .success(false)
                                .message("File is empty")
                                .build());
            }

            String objectName = minIOService.uploadImage(file);

            ImageUploadResponse response = ImageUploadResponse.builder()
                    .success(true)
                    .message("Image uploaded successfully")
                    .objectName(objectName)
                    .fileName(file.getOriginalFilename())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .success(false)
                            .message("Error uploading image: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteImage(@RequestParam("objectName") String objectName) {
        try {
            minIOService.deleteImage(objectName);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ErrorResponse.builder()
                            .success(true)
                            .message("Image deleted successfully")
                            .build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .success(false)
                            .message("Error deleting image: " + e.getMessage())
                            .build());
        }
    }
}

