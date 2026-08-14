package com.supremecourt.studentgradingsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseStatus {
    ACTIVE, INACTIVE;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return CourseStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
