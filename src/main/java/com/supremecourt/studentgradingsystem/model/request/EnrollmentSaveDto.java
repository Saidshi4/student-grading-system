package com.supremecourt.studentgradingsystem.model.request;

import com.supremecourt.studentgradingsystem.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentSaveDto {
    @NotNull
    private Long studentId;
    @NotNull
    private Long courseOfferingId;
    private EnrollmentStatus status;
}
