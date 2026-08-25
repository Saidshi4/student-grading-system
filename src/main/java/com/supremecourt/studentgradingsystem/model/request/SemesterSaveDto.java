package com.supremecourt.studentgradingsystem.model.request;

import com.supremecourt.studentgradingsystem.enums.SemesterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterSaveDto {
    @NotBlank
    private String academicYear;
    @NotNull
    private SemesterType semesterType;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
}
