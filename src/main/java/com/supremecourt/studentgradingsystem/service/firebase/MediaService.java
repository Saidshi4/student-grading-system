package com.supremecourt.studentgradingsystem.service.firebase;

import com.supremecourt.studentgradingsystem.exception.FirebaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaService {

    private final FirebaseService firebaseService;

    public String uploadToFirebase(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) return null;

        String fileName = Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_")
                + System.currentTimeMillis() + "." + type;

        try {
            return firebaseService.uploadMedia(file.getInputStream(), fileName, file.getContentType());
        } catch (Exception e) {
            log.error("Failed to upload file: {}", fileName, e);
            throw new FirebaseException("UPLOAD_FAILED", "Failed to upload file: " + fileName);
        }
    }

    public void deleteFromFirebase(String URL) {
        try {
            firebaseService.deleteMedia(URL);
        } catch (Exception e) {
            log.error("Failed to delete media from Firebase: {}", URL, e);
            throw new FirebaseException("DELETE_FAILED", "Failed to delete media from Firebase: " + URL);
        }
    }
}