package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.TeacherEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.TeacherRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.IsNotEmptyException;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.TeacherMapper;
import com.supremecourt.studentgradingsystem.model.request.TeacherSaveDto;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.response.TeacherGetDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final UserService userService;

    @Transactional
    public TeacherGetDto create(TeacherSaveDto dto) {
        log.info("ActionLog.createTeacher.start");
        UserRegistrationDto registrationDto = UserRegistrationDto.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .birthDate(dto.getBirthDate())
                .role("TEACHER")
                .department(dto.getDepartment())
                .build();
        UserEntity saved = userService.createUser(registrationDto);
        TeacherGetDto result = teacherMapper.mapEntityToGetDto((TeacherEntity) saved);
        log.info("ActionLog.createTeacher.end");
        return result;
    }

    public List<TeacherGetDto> getAll() {
        log.info("ActionLog.getAllTeachers.start");
        List<TeacherGetDto> result = teacherMapper.mapEntityToGetDtos(teacherRepository.findAll());
        log.info("ActionLog.getAllTeachers.end");
        return result;
    }

    public TeacherGetDto getById(Long id) {
        log.info("ActionLog.getTeacherById.start id {}", id);
        TeacherGetDto result = teacherMapper.mapEntityToGetDto(findEntity(id));
        log.info("ActionLog.getTeacherById.end id {}", id);
        return result;
    }

    @Transactional
    public TeacherGetDto update(Long id, TeacherSaveDto dto) {
        log.info("ActionLog.updateTeacher.start id {}", id);
        TeacherEntity entity = findEntity(id);
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setBirthDate(dto.getBirthDate());
        entity.setDepartment(dto.getDepartment());
        TeacherGetDto result = teacherMapper.mapEntityToGetDto(teacherRepository.save(entity));
        log.info("ActionLog.updateTeacher.end id {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteTeacher.start id {}", id);
        TeacherEntity entity = findEntity(id);
        if (entity.getCourseOfferings() != null && !entity.getCourseOfferings().isEmpty()) {
            throw new IsNotEmptyException("Teacher has course offerings and cannot be deleted",
                    "ActionLog.deleteTeacher.error teacher " + id + " has course offerings");
        }
        teacherRepository.delete(entity);
        log.info("ActionLog.deleteTeacher.end id {}", id);
    }

    public TeacherEntity findEntity(Long id) {
        return teacherRepository.findById(id).orElseThrow(() -> new NotFoundException(
                ExceptionEnum.TEACHER_NOT_FOUND.getMessage(),
                String.format(ExceptionEnum.TEACHER_NOT_FOUND.getLog(), id)
        ));
    }
}
