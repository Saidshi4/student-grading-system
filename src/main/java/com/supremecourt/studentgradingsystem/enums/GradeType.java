package com.supremecourt.studentgradingsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GradeType {
    MIDTERM, ASSIGNMENT, QUIZ, FINAL;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GradeType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return GradeType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
