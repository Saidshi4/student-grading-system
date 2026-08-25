package com.supremecourt.studentgradingsystem.model.response;

import com.supremecourt.studentgradingsystem.enums.GradeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeGetDto {
    private Long id;
    private Integer score;
    private GradeType type;
    private Long enrollmentId;
    private String studentUsername;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
