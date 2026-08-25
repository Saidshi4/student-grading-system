package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.CourseOfferingEntity;
import com.supremecourt.studentgradingsystem.dao.repository.CourseOfferingRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.CourseOfferingMapper;
import com.supremecourt.studentgradingsystem.model.request.CourseOfferingSaveDto;
import com.supremecourt.studentgradingsystem.model.response.CourseOfferingGetDto;
import com.supremecourt.studentgradingsystem.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseOfferingService {
    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseOfferingMapper courseOfferingMapper;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final SemesterService semesterService;
    private final GroupService groupService;

    @Transactional
    public CourseOfferingGetDto create(CourseOfferingSaveDto dto) {
        log.info("ActionLog.createCourseOffering.start");
        CourseOfferingEntity entity = new CourseOfferingEntity();
        applyDto(entity, dto);
        CourseOfferingGetDto result = courseOfferingMapper.mapEntityToGetDto(courseOfferingRepository.save(entity));
        log.info("ActionLog.createCourseOffering.end");
        return result;
    }

    @Transactional(readOnly = true)
    public List<CourseOfferingGetDto> getAll(Long teacherId, Long semesterId, Long groupId) {
        log.info("ActionLog.getAllCourseOfferings.start");
        if (SecurityUtils.isTeacher()) {
            teacherId = SecurityUtils.getCurrentUserId();
        }
        List<CourseOfferingEntity> entities;
        if (teacherId != null) {
            entities = courseOfferingRepository.findByTeacherId(teacherId);
        } else if (semesterId != null) {
            entities = courseOfferingRepository.findBySemesterId(semesterId);
        } else if (groupId != null) {
            entities = courseOfferingRepository.findByGroupId(groupId);
        } else {
            entities = courseOfferingRepository.findAll();
        }
        List<CourseOfferingGetDto> result = courseOfferingMapper.mapEntityToGetDtos(entities);
        log.info("ActionLog.getAllCourseOfferings.end");
        return result;
    }

    @Transactional(readOnly = true)
    public CourseOfferingGetDto getById(Long id) {
        log.info("ActionLog.getCourseOfferingById.start id {}", id);
        CourseOfferingEntity entity = findEntity(id);
        SecurityUtils.ensureTeacherOwnsCourse(entity.getTeacher().getId());
        CourseOfferingGetDto result = courseOfferingMapper.mapEntityToGetDto(entity);
        log.info("ActionLog.getCourseOfferingById.end id {}", id);
        return result;
    }

    @Transactional
    public CourseOfferingGetDto update(Long id, CourseOfferingSaveDto dto) {
        log.info("ActionLog.updateCourseOffering.start id {}", id);
        CourseOfferingEntity entity = findEntity(id);
        applyDto(entity, dto);
        CourseOfferingGetDto result = courseOfferingMapper.mapEntityToGetDto(courseOfferingRepository.save(entity));
        log.info("ActionLog.updateCourseOffering.end id {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteCourseOffering.start id {}", id);
        courseOfferingRepository.delete(findEntity(id));
        log.info("ActionLog.deleteCourseOffering.end id {}", id);
    }

    public CourseOfferingEntity findEntity(Long id) {
        return courseOfferingRepository.findById(id).orElseThrow(() -> new NotFoundException(
                ExceptionEnum.COURSE_OFFERING_NOT_FOUND.getMessage(),
                String.format(ExceptionEnum.COURSE_OFFERING_NOT_FOUND.getLog(), id)
        ));
    }

    private void applyDto(CourseOfferingEntity entity, CourseOfferingSaveDto dto) {
        entity.setCapacity(dto.getCapacity());
        entity.setStatus(dto.getStatus());
        entity.setEnrollmentType(dto.getEnrollmentType());
        entity.setTeacher(teacherService.findEntity(dto.getTeacherId()));
        entity.setSubject(subjectService.findEntity(dto.getSubjectId()));
        entity.setSemester(semesterService.findEntity(dto.getSemesterId()));
        entity.setGroup(dto.getGroupId() == null ? null : groupService.findEntity(dto.getGroupId()));
    }
}
