package com.supremecourt.studentgradingsystem.dao.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "teachers")
@DiscriminatorValue("TEACHER")
@PrimaryKeyJoinColumn(name = "id")
public class TeacherEntity extends UserEntity {

    private String department;

    @OneToMany(mappedBy = "teacher")
    private List<CourseOfferingEntity> courseOfferings;
}
