package com.supremecourt.studentgradingsystem.dao.entity;

import com.supremecourt.studentgradingsystem.enums.CourseStatus;
import com.supremecourt.studentgradingsystem.enums.EnrollmentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "course_offerings")
public class CourseOfferingEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer capacity;
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
    @Enumerated(EnumType.STRING)
    private EnrollmentType enrollmentType;

    @ManyToOne
    private TeacherEntity teacher;

    @ManyToOne
    private SubjectEntity subject;

    @ManyToOne
    private SemesterEntity semester;

    @ManyToOne
    private GroupEntity group;

}
