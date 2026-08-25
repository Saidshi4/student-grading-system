package com.supremecourt.studentgradingsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EnrollmentStatus {
    ENROLLED, DROPPED;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static EnrollmentStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return EnrollmentStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
