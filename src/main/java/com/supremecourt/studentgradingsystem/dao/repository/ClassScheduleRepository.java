package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.ClassScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassScheduleEntity, Long> {
    List<ClassScheduleEntity> findByCourseOfferingId(Long courseOfferingId);
}
