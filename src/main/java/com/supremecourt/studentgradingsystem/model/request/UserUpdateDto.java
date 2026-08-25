package com.supremecourt.studentgradingsystem.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
}
