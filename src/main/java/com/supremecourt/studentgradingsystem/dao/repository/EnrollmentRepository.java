package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.EnrollmentEntity;
import com.supremecourt.studentgradingsystem.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
    boolean existsByStudentIdAndCourseOfferingId(Long studentId, Long courseOfferingId);

    List<EnrollmentEntity> findByStudentId(Long studentId);

    List<EnrollmentEntity> findByCourseOfferingId(Long courseOfferingId);

    List<EnrollmentEntity> findByCourseOfferingTeacherId(Long teacherId);

    List<EnrollmentEntity> findByStudentIdAndCourseOfferingTeacherId(Long studentId, Long teacherId);

    boolean existsByStudentIdAndCourseOfferingTeacherId(Long studentId, Long teacherId);

    long countByCourseOfferingIdAndStatus(Long courseOfferingId, EnrollmentStatus status);

    @Query("""
            SELECT DISTINCT e FROM EnrollmentEntity e
            LEFT JOIN FETCH e.grades
            LEFT JOIN FETCH e.courseOffering co
            LEFT JOIN FETCH co.subject
            WHERE e.student.id = :studentId
            """)
    List<EnrollmentEntity> findByStudentIdWithGrades(@Param("studentId") Long studentId);

    @Query("""
            SELECT DISTINCT e FROM EnrollmentEntity e
            LEFT JOIN FETCH e.grades
            LEFT JOIN FETCH e.student
            WHERE e.courseOffering.id = :courseOfferingId
            """)
    List<EnrollmentEntity> findByCourseOfferingIdWithGrades(@Param("courseOfferingId") Long courseOfferingId);
}
