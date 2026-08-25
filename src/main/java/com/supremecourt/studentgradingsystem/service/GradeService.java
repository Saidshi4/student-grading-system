package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.EnrollmentEntity;
import com.supremecourt.studentgradingsystem.dao.entity.GradeEntity;
import com.supremecourt.studentgradingsystem.dao.repository.GradeRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.exception.UserAlreadyExistsException;
import com.supremecourt.studentgradingsystem.mapper.GradeMapper;
import com.supremecourt.studentgradingsystem.model.request.GradeSaveDto;
import com.supremecourt.studentgradingsystem.model.response.GradeGetDto;
import com.supremecourt.studentgradingsystem.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;
    private final EnrollmentService enrollmentService;

    @Transactional
    public GradeGetDto create(GradeSaveDto dto) {
        log.info("ActionLog.createGrade.start");
        EnrollmentEntity enrollment = enrollmentService.findEntity(dto.getEnrollmentId());
        SecurityUtils.ensureTeacherOwnsCourse(enrollment.getCourseOffering().getTeacher().getId());
        if (gradeRepository.existsByEnrollmentIdAndType(dto.getEnrollmentId(), dto.getType())) {
            throw new UserAlreadyExistsException(
                    ExceptionEnum.GRADE_ALREADY_EXISTS.getMessage(),
                    ExceptionEnum.GRADE_ALREADY_EXISTS.getLog()
            );
        }
        GradeEntity entity = new GradeEntity();
        entity.setScore(dto.getScore());
        entity.setType(dto.getType());
        entity.setEnrollment(enrollment);
        GradeGetDto result = gradeMapper.mapEntityToGetDto(gradeRepository.save(entity));
        log.info("ActionLog.createGrade.end");
        return result;
    }

    @Transactional(readOnly = true)
    public List<GradeGetDto> getAll(Long enrollmentId) {
        log.info("ActionLog.getAllGrades.start");
        List<GradeEntity> entities;
        if (SecurityUtils.isStudent()) {
            Long studentId = SecurityUtils.getCurrentUserId();
            if (enrollmentId != null) {
                entities = gradeRepository.findByEnrollmentIdAndStudentId(enrollmentId, studentId);
            } else {
                entities = gradeRepository.findByStudentId(studentId);
            }
        } else if (SecurityUtils.isTeacher()) {
            Long teacherId = SecurityUtils.getCurrentUserId();
            if (enrollmentId != null) {
                EnrollmentEntity enrollment = enrollmentService.findEntity(enrollmentId);
                SecurityUtils.ensureTeacherOwnsCourse(enrollment.getCourseOffering().getTeacher().getId());
                entities = gradeRepository.findByEnrollmentIdAndTeacherId(enrollmentId, teacherId);
            } else {
                entities = gradeRepository.findByTeacherId(teacherId);
            }
        } else if (enrollmentId != null) {
            entities = gradeRepository.findByEnrollmentId(enrollmentId);
        } else {
            entities = gradeRepository.findAll();
        }
        List<GradeGetDto> result = gradeMapper.mapEntityToGetDtos(entities);
        log.info("ActionLog.getAllGrades.end");
        return result;
    }

    @Transactional(readOnly = true)
    public GradeGetDto getById(Long id) {
        log.info("ActionLog.getGradeById.start id {}", id);
        GradeEntity entity = findEntity(id);
        SecurityUtils.ensureStudentOwnsResource(entity.getEnrollment().getStudent().getId());
        SecurityUtils.ensureTeacherOwnsCourse(entity.getEnrollment().getCourseOffering().getTeacher().getId());
        GradeGetDto result = gradeMapper.mapEntityToGetDto(entity);
        log.info("ActionLog.getGradeById.end id {}", id);
        return result;
    }

    @Transactional
    public GradeGetDto update(Long id, GradeSaveDto dto) {
        log.info("ActionLog.updateGrade.start id {}", id);
        GradeEntity entity = findEntity(id);
        EnrollmentEntity enrollment = enrollmentService.findEntity(dto.getEnrollmentId());
        SecurityUtils.ensureTeacherOwnsCourse(enrollment.getCourseOffering().getTeacher().getId());
        if (gradeRepository.existsByEnrollmentIdAndTypeAndIdNot(dto.getEnrollmentId(), dto.getType(), id)) {
            throw new UserAlreadyExistsException(
                    ExceptionEnum.GRADE_ALREADY_EXISTS.getMessage(),
                    ExceptionEnum.GRADE_ALREADY_EXISTS.getLog()
            );
        }
        entity.setScore(dto.getScore());
        entity.setType(dto.getType());
        entity.setEnrollment(enrollment);
        GradeGetDto result = gradeMapper.mapEntityToGetDto(gradeRepository.save(entity));
        log.info("ActionLog.updateGrade.end id {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteGrade.start id {}", id);
        GradeEntity entity = findEntity(id);
        SecurityUtils.ensureTeacherOwnsCourse(entity.getEnrollment().getCourseOffering().getTeacher().getId());
        gradeRepository.delete(entity);
        log.info("ActionLog.deleteGrade.end id {}", id);
    }

    private GradeEntity findEntity(Long id) {
        return gradeRepository.findById(id).orElseThrow(() -> new NotFoundException(
                ExceptionEnum.GRADE_NOT_FOUND.getMessage(),
                String.format(ExceptionEnum.GRADE_NOT_FOUND.getLog(), id)
        ));
    }
}
