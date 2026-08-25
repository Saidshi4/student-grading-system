package com.supremecourt.studentgradingsystem.model.response;

import com.supremecourt.studentgradingsystem.enums.CourseStatus;
import com.supremecourt.studentgradingsystem.enums.EnrollmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseOfferingGetDto {
    private Long id;
    private Integer capacity;
    private CourseStatus status;
    private EnrollmentType enrollmentType;
    private Long teacherId;
    private String teacherUsername;
    private Long subjectId;
    private String subjectName;
    private Long semesterId;
    private String academicYear;
    private Long groupId;
    private String groupName;
}
