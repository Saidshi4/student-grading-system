package com.supremecourt.studentgradingsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SemesterType {
    FALL, SPRING, SUMMER;;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SemesterType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return SemesterType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
