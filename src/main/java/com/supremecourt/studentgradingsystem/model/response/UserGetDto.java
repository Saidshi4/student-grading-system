package com.supremecourt.studentgradingsystem.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGetDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
    private String imageUrl;
    private LocalDate birthDate;
    private String role;
}
