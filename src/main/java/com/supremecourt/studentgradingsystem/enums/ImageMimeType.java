package com.supremecourt.studentgradingsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ImageMimeType {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    WEBP("image/webp"),
    BMP("image/bmp"),
    SVG("image/svg+xml"),
    TIFF("image/tiff");

    private final String type;

    ImageMimeType(String type) {
        this.type = type;
    }

    public static boolean isImage(String contentType) {
        for (ImageMimeType imageMimeType : values()) {
            if (imageMimeType.getType().equals(contentType)) {
                return true;
            }
        }
        return false;
    }
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ImageMimeType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return ImageMimeType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
