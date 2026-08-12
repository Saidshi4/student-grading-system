package com.supremecourt.studentgradingsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CourseStatusEnum {
    ACTIVE, INACTIVE;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CourseStatusEnum fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return CourseStatusEnum.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
