package com.supremecourt.studentgradingsystem.model.request;

import com.supremecourt.studentgradingsystem.enums.CourseStatus;
import com.supremecourt.studentgradingsystem.enums.EnrollmentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseOfferingSaveDto {
    private Integer capacity;
    private CourseStatus status;
    private EnrollmentType enrollmentType;
    @NotNull
    private Long teacherId;
    @NotNull
    private Long subjectId;
    @NotNull
    private Long semesterId;
    private Long groupId;
}
