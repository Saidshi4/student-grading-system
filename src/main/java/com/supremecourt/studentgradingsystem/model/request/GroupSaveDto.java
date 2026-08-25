package com.supremecourt.studentgradingsystem.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupSaveDto {
    @NotBlank
    private String name;
    private String code;
    private String program;
    private String year;
}
