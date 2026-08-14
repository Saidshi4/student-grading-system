package com.supremecourt.studentgradingsystem.dao.entity;

import com.supremecourt.studentgradingsystem.enums.SemesterType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumeratedValue;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "semesters")
public class SemesterEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String academicYear;
    @Enumerated(EnumType.STRING)
    private SemesterType semesterType;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "semester")
    private List<CourseOfferingEntity> courseOfferings;
}
