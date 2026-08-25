package com.supremecourt.studentgradingsystem.dao.repository;

import com.supremecourt.studentgradingsystem.dao.entity.GradeEntity;
import com.supremecourt.studentgradingsystem.enums.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<GradeEntity, Long> {
    List<GradeEntity> findByEnrollmentId(Long enrollmentId);

    @Query("SELECT g FROM GradeEntity g WHERE g.enrollment.student.id = :studentId")
    List<GradeEntity> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT g FROM GradeEntity g WHERE g.enrollment.id = :enrollmentId AND g.enrollment.student.id = :studentId")
    List<GradeEntity> findByEnrollmentIdAndStudentId(@Param("enrollmentId") Long enrollmentId,
                                                     @Param("studentId") Long studentId);

    @Query("SELECT g FROM GradeEntity g WHERE g.enrollment.courseOffering.teacher.id = :teacherId")
    List<GradeEntity> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("""
            SELECT g FROM GradeEntity g
            WHERE g.enrollment.id = :enrollmentId
              AND g.enrollment.courseOffering.teacher.id = :teacherId
            """)
    List<GradeEntity> findByEnrollmentIdAndTeacherId(@Param("enrollmentId") Long enrollmentId,
                                                     @Param("teacherId") Long teacherId);

    boolean existsByEnrollmentIdAndType(Long enrollmentId, GradeType type);

    boolean existsByEnrollmentIdAndTypeAndIdNot(Long enrollmentId, GradeType type, Long id);
}
