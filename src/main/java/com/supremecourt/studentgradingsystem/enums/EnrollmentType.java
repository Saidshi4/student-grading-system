package com.supremecourt.studentgradingsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EnrollmentType {
    MANDATORY, ELECTIVE;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static EnrollmentType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return EnrollmentType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
