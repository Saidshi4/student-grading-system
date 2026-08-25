package com.supremecourt.studentgradingsystem.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectSaveDto {
    @NotBlank
    private String name;
    @NotBlank
    private String code;
    private String description;
    @NotNull
    private Long credits;
}
