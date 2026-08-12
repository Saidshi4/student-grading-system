package com.supremecourt.studentgradingsystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EntranceType {
    LOGIN,
    SIGNUP;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static EntranceType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return EntranceType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}