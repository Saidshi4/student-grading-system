package com.supremecourt.studentgradingsystem.model.response;

import com.supremecourt.studentgradingsystem.enums.SemesterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterGetDto {
    private Long id;
    private String academicYear;
    private SemesterType semesterType;
    private LocalDate startDate;
    private LocalDate endDate;
}
