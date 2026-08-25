package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.CourseOfferingEntity;
import com.supremecourt.studentgradingsystem.dao.entity.EnrollmentEntity;
import com.supremecourt.studentgradingsystem.dao.repository.EnrollmentRepository;
import com.supremecourt.studentgradingsystem.enums.EnrollmentStatus;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.exception.UserAlreadyExistsException;
import com.supremecourt.studentgradingsystem.mapper.EnrollmentMapper;
import com.supremecourt.studentgradingsystem.model.request.EnrollmentSaveDto;
import com.supremecourt.studentgradingsystem.model.response.EnrollmentGetDto;
import com.supremecourt.studentgradingsystem.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final StudentService studentService;
    private final CourseOfferingService courseOfferingService;

    @Transactional
    public EnrollmentGetDto create(EnrollmentSaveDto dto) {
        log.info("ActionLog.createEnrollment.start");
        if (enrollmentRepository.existsByStudentIdAndCourseOfferingId(dto.getStudentId(), dto.getCourseOfferingId())) {
            throw new UserAlreadyExistsException(
                    ExceptionEnum.ENROLLMENT_ALREADY_EXISTS.getMessage(),
                    ExceptionEnum.ENROLLMENT_ALREADY_EXISTS.getLog()
            );
        }
        CourseOfferingEntity offering = courseOfferingService.findEntity(dto.getCourseOfferingId());
        ensureCapacity(offering);
        EnrollmentEntity entity = new EnrollmentEntity();
        entity.setStudent(studentService.findEntity(dto.getStudentId()));
        entity.setCourseOffering(offering);
        entity.setStatus(dto.getStatus() == null ? EnrollmentStatus.ENROLLED : dto.getStatus());
        entity.setEnrolledAt(Instant.now());
        if (entity.getStatus() == EnrollmentStatus.DROPPED) {
            entity.setDroppedAt(Instant.now());
        }
        EnrollmentGetDto result = toDto(enrollmentRepository.save(entity));
        log.info("ActionLog.createEnrollment.end");
        return result;
    }

    @Transactional(readOnly = true)
    public List<EnrollmentGetDto> getAll(Long studentId, Long courseOfferingId) {
        log.info("ActionLog.getAllEnrollments.start");
        if (SecurityUtils.isStudent()) {
            Long currentStudentId = SecurityUtils.getCurrentUserId();
            if (studentId != null && !studentId.equals(currentStudentId)) {
                SecurityUtils.deny();
            }
            studentId = currentStudentId;
        }
        List<EnrollmentEntity> entities;
        if (SecurityUtils.isTeacher()) {
            Long teacherId = SecurityUtils.getCurrentUserId();
            if (studentId != null) {
                entities = enrollmentRepository.findByStudentIdAndCourseOfferingTeacherId(studentId, teacherId);
            } else if (courseOfferingId != null) {
                SecurityUtils.ensureTeacherOwnsCourse(courseOfferingService.findEntity(courseOfferingId).getTeacher().getId());
                entities = enrollmentRepository.findByCourseOfferingId(courseOfferingId);
            } else {
                entities = enrollmentRepository.findByCourseOfferingTeacherId(teacherId);
            }
        } else if (studentId != null) {
            entities = enrollmentRepository.findByStudentId(studentId);
            if (courseOfferingId != null) {
                Long offeringId = courseOfferingId;
                entities = entities.stream()
                        .filter(e -> e.getCourseOffering().getId().equals(offeringId))
                        .toList();
            }
        } else if (courseOfferingId != null) {
            entities = enrollmentRepository.findByCourseOfferingId(courseOfferingId);
        } else {
            entities = enrollmentRepository.findAll();
        }
        List<EnrollmentGetDto> result = entities.stream().map(this::toDto).toList();
        log.info("ActionLog.getAllEnrollments.end");
        return result;
    }

    @Transactional(readOnly = true)
    public EnrollmentGetDto getById(Long id) {
        log.info("ActionLog.getEnrollmentById.start id {}", id);
        EnrollmentEntity entity = findEntity(id);
        SecurityUtils.ensureStudentOwnsResource(entity.getStudent().getId());
        SecurityUtils.ensureTeacherOwnsCourse(entity.getCourseOffering().getTeacher().getId());
        EnrollmentGetDto result = toDto(entity);
        log.info("ActionLog.getEnrollmentById.end id {}", id);
        return result;
    }

    @Transactional
    public EnrollmentGetDto update(Long id, EnrollmentSaveDto dto) {
        log.info("ActionLog.updateEnrollment.start id {}", id);
        EnrollmentEntity entity = findEntity(id);
        if (!entity.getStudent().getId().equals(dto.getStudentId())
                || !entity.getCourseOffering().getId().equals(dto.getCourseOfferingId())) {
            if (enrollmentRepository.existsByStudentIdAndCourseOfferingId(dto.getStudentId(), dto.getCourseOfferingId())) {
                throw new UserAlreadyExistsException(
                        ExceptionEnum.ENROLLMENT_ALREADY_EXISTS.getMessage(),
                        ExceptionEnum.ENROLLMENT_ALREADY_EXISTS.getLog()
                );
            }
        }
        CourseOfferingEntity offering = courseOfferingService.findEntity(dto.getCourseOfferingId());
        EnrollmentStatus nextStatus = dto.getStatus() == null ? entity.getStatus() : dto.getStatus();
        if (entity.getStatus() != EnrollmentStatus.ENROLLED && nextStatus == EnrollmentStatus.ENROLLED) {
            ensureCapacity(offering);
        }
        entity.setStudent(studentService.findEntity(dto.getStudentId()));
        entity.setCourseOffering(offering);
        entity.setStatus(nextStatus);
        if (nextStatus == EnrollmentStatus.DROPPED && entity.getDroppedAt() == null) {
            entity.setDroppedAt(Instant.now());
        }
        if (nextStatus == EnrollmentStatus.ENROLLED) {
            entity.setDroppedAt(null);
        }
        EnrollmentGetDto result = toDto(enrollmentRepository.save(entity));
        log.info("ActionLog.updateEnrollment.end id {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteEnrollment.start id {}", id);
        enrollmentRepository.delete(findEntity(id));
        log.info("ActionLog.deleteEnrollment.end id {}", id);
    }

    public EnrollmentEntity findEntity(Long id) {
        return enrollmentRepository.findById(id).orElseThrow(() -> new NotFoundException(
                ExceptionEnum.ENROLLMENT_NOT_FOUND.getMessage(),
                String.format(ExceptionEnum.ENROLLMENT_NOT_FOUND.getLog(), id)
        ));
    }

    private EnrollmentGetDto toDto(EnrollmentEntity entity) {
        EnrollmentGetDto dto = enrollmentMapper.mapEntityToGetDto(entity);
        dto.setFinalScore(FinalScoreCalculator.calculate(entity.getGrades()));
        return dto;
    }

    private void ensureCapacity(CourseOfferingEntity offering) {
        if (offering.getCapacity() == null) {
            return;
        }
        long enrolled = enrollmentRepository.countByCourseOfferingIdAndStatus(offering.getId(), EnrollmentStatus.ENROLLED);
        if (enrolled >= offering.getCapacity()) {
            throw new UserAlreadyExistsException(
                    ExceptionEnum.COURSE_OFFERING_FULL.getMessage(),
                    String.format(ExceptionEnum.COURSE_OFFERING_FULL.getLog(), offering.getId())
            );
        }
    }
}
