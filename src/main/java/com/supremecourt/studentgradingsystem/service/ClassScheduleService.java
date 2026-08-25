package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.ClassScheduleEntity;
import com.supremecourt.studentgradingsystem.dao.repository.ClassScheduleRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.ClassScheduleMapper;
import com.supremecourt.studentgradingsystem.model.request.ClassScheduleSaveDto;
import com.supremecourt.studentgradingsystem.model.response.ClassScheduleGetDto;
import com.supremecourt.studentgradingsystem.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClassScheduleService {
    private final ClassScheduleRepository classScheduleRepository;
    private final ClassScheduleMapper classScheduleMapper;
    private final CourseOfferingService courseOfferingService;

    @Transactional
    public ClassScheduleGetDto create(ClassScheduleSaveDto dto) {
        log.info("ActionLog.createClassSchedule.start");
        validateTimes(dto);
        ClassScheduleEntity entity = new ClassScheduleEntity();
        applyDto(entity, dto);
        ClassScheduleGetDto result = classScheduleMapper.mapEntityToGetDto(classScheduleRepository.save(entity));
        log.info("ActionLog.createClassSchedule.end");
        return result;
    }

    @Transactional(readOnly = true)
    public List<ClassScheduleGetDto> getAll(Long courseOfferingId) {
        log.info("ActionLog.getAllClassSchedules.start");
        List<ClassScheduleEntity> entities;
        if (SecurityUtils.isTeacher()) {
            Long teacherId = SecurityUtils.getCurrentUserId();
            if (courseOfferingId != null) {
                SecurityUtils.ensureTeacherOwnsCourse(courseOfferingService.findEntity(courseOfferingId).getTeacher().getId());
                entities = classScheduleRepository.findByCourseOfferingId(courseOfferingId);
            } else {
                entities = classScheduleRepository.findAll().stream()
                        .filter(schedule -> schedule.getCourseOffering().getTeacher().getId().equals(teacherId))
                        .toList();
            }
        } else if (courseOfferingId != null) {
            entities = classScheduleRepository.findByCourseOfferingId(courseOfferingId);
        } else {
            entities = classScheduleRepository.findAll();
        }
        List<ClassScheduleGetDto> result = classScheduleMapper.mapEntityToGetDtos(entities);
        log.info("ActionLog.getAllClassSchedules.end");
        return result;
    }

    @Transactional(readOnly = true)
    public ClassScheduleGetDto getById(Long id) {
        log.info("ActionLog.getClassScheduleById.start id {}", id);
        ClassScheduleEntity entity = findEntity(id);
        SecurityUtils.ensureTeacherOwnsCourse(entity.getCourseOffering().getTeacher().getId());
        ClassScheduleGetDto result = classScheduleMapper.mapEntityToGetDto(entity);
        log.info("ActionLog.getClassScheduleById.end id {}", id);
        return result;
    }

    @Transactional
    public ClassScheduleGetDto update(Long id, ClassScheduleSaveDto dto) {
        log.info("ActionLog.updateClassSchedule.start id {}", id);
        validateTimes(dto);
        ClassScheduleEntity entity = findEntity(id);
        applyDto(entity, dto);
        ClassScheduleGetDto result = classScheduleMapper.mapEntityToGetDto(classScheduleRepository.save(entity));
        log.info("ActionLog.updateClassSchedule.end id {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteClassSchedule.start id {}", id);
        classScheduleRepository.delete(findEntity(id));
        log.info("ActionLog.deleteClassSchedule.end id {}", id);
    }

    private void applyDto(ClassScheduleEntity entity, ClassScheduleSaveDto dto) {
        entity.setDay(dto.getDay());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setRoom(dto.getRoom());
        entity.setCourseOffering(courseOfferingService.findEntity(dto.getCourseOfferingId()));
    }

    private void validateTimes(ClassScheduleSaveDto dto) {
        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IllegalArgumentException(ExceptionEnum.INVALID_TIME_RANGE.getMessage());
        }
    }

    private ClassScheduleEntity findEntity(Long id) {
        return classScheduleRepository.findById(id).orElseThrow(() -> new NotFoundException(
                ExceptionEnum.CLASS_SCHEDULE_NOT_FOUND.getMessage(),
                String.format(ExceptionEnum.CLASS_SCHEDULE_NOT_FOUND.getLog(), id)
        ));
    }
}
