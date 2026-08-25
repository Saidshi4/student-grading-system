package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.CourseOfferingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseOfferingRepository extends JpaRepository<CourseOfferingEntity, Long> {
    List<CourseOfferingEntity> findByTeacherId(Long teacherId);

    List<CourseOfferingEntity> findBySemesterId(Long semesterId);

    List<CourseOfferingEntity> findByGroupId(Long groupId);
}
