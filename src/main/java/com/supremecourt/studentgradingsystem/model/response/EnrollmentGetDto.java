package com.supremecourt.studentgradingsystem.model.response;

import com.supremecourt.studentgradingsystem.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentGetDto {
    private Long id;
    private Long studentId;
    private String studentUsername;
    private Long courseOfferingId;
    private String subjectName;
    private EnrollmentStatus status;
    private Instant enrolledAt;
    private Instant droppedAt;
    private Double finalScore;
}
