package com.supremecourt.studentgradingsystem.service.firebase;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.supremecourt.studentgradingsystem.enums.ImageMimeType;
import com.supremecourt.studentgradingsystem.exception.ContentTypeException;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FirebaseService {

    public String uploadMedia(InputStream fileStream, String originalFileName, String contentType) {

        if (!ImageMimeType.isImage(contentType)) {
            throw new ContentTypeException(
                    "Unsupported Content Type: " + contentType,
                    "Unsupported Content Type: " + contentType
            );
        }

        String fileName = "images/" + UUID.randomUUID() + "/" + originalFileName;

        Bucket bucket = StorageClient.getInstance().bucket();
        bucket.create(fileName, fileStream, contentType);

        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                fileName.replace("/", "%2F")
        );
    }

    public void deleteMedia(String url) {

        String filePath = extractFilePath(url);

        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(filePath);

        if (blob == null) {
            throw new NotFoundException(
                    "Media Not Found!",
                    "Media Not Found!"
            );
        }

        blob.delete();
    }

    private String extractFilePath(String url) {

        int start = url.indexOf("/o/") + 3;
        int end = url.indexOf("?alt=media");

        return url.substring(start, end)
                .replace("%2F", "/");
    }
}