package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.StudentEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.EnrollmentRepository;
import com.supremecourt.studentgradingsystem.dao.repository.StudentRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.StudentMapper;
import com.supremecourt.studentgradingsystem.model.request.StudentSaveDto;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.response.StudentGetDto;
import com.supremecourt.studentgradingsystem.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final UserService userService;
    private final GroupService groupService;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public StudentGetDto create(StudentSaveDto dto) {
        log.info("ActionLog.createStudent.start");
        UserRegistrationDto registrationDto = UserRegistrationDto.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .birthDate(dto.getBirthDate())
                .role("STUDENT")
                .groupId(dto.getGroupId())
                .build();
        UserEntity saved = userService.createUser(registrationDto);
        StudentGetDto result = studentMapper.mapEntityToGetDto((StudentEntity) saved);
        log.info("ActionLog.createStudent.end");
        return result;
    }

    @Transactional(readOnly = true)
    public List<StudentGetDto> getAll(String firstName, String lastName, String username, String name, Long groupId) {
        log.info("ActionLog.getAllStudents.start");
        if (SecurityUtils.isStudent()) {
            List<StudentGetDto> own = List.of(studentMapper.mapEntityToGetDto(findEntity(SecurityUtils.getCurrentUserId())));
            log.info("ActionLog.getAllStudents.end");
            return own;
        }
        Specification<StudentEntity> spec = (root, query, cb) -> cb.conjunction();
        if (SecurityUtils.isTeacher()) {
            Long teacherId = SecurityUtils.getCurrentUserId();
            spec = spec.and((root, query, cb) -> {
                var subquery = query.subquery(Long.class);
                var enrollment = subquery.from(com.supremecourt.studentgradingsystem.dao.entity.EnrollmentEntity.class);
                subquery.select(enrollment.get("student").get("id"))
                        .where(cb.equal(enrollment.get("courseOffering").get("teacher").get("id"), teacherId));
                return root.get("id").in(subquery);
            });
        }
        if (StringUtils.hasText(firstName)) {
            spec = spec.and(likeIgnoreCase("firstName", firstName));
        }
        if (StringUtils.hasText(lastName)) {
            spec = spec.and(likeIgnoreCase("lastName", lastName));
        }
        if (StringUtils.hasText(username)) {
            spec = spec.and(likeIgnoreCase("username", username));
        }
        if (StringUtils.hasText(name)) {
            String pattern = contains(name);
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern),
                    cb.like(cb.lower(cb.concat(cb.concat(root.get("firstName"), " "), root.get("lastName"))), pattern)
            ));
        }
        if (groupId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("group").get("id"), groupId));
        }
        List<StudentGetDto> result = studentMapper.mapEntityToGetDtos(studentRepository.findAll(spec));
        log.info("ActionLog.getAllStudents.end");
        return result;
    }

    @Transactional(readOnly = true)
    public StudentGetDto getById(Long id) {
        log.info("ActionLog.getStudentById.start id {}", id);
        SecurityUtils.ensureStudentOwnsResource(id);
        if (SecurityUtils.isTeacher()
                && !enrollmentRepository.existsByStudentIdAndCourseOfferingTeacherId(id, SecurityUtils.getCurrentUserId())) {
            SecurityUtils.deny();
        }
        StudentGetDto result = studentMapper.mapEntityToGetDto(findEntity(id));
        log.info("ActionLog.getStudentById.end id {}", id);
        return result;
    }

    @Transactional
    public StudentGetDto update(Long id, StudentSaveDto dto) {
        log.info("ActionLog.updateStudent.start id {}", id);
        StudentEntity entity = findEntity(id);
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setBirthDate(dto.getBirthDate());
        entity.setGroup(dto.getGroupId() == null ? null : groupService.findEntity(dto.getGroupId()));
        StudentGetDto result = studentMapper.mapEntityToGetDto(studentRepository.save(entity));
        log.info("ActionLog.updateStudent.end id {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteStudent.start id {}", id);
        studentRepository.delete(findEntity(id));
        log.info("ActionLog.deleteStudent.end id {}", id);
    }

    public StudentEntity findEntity(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new NotFoundException(
                ExceptionEnum.STUDENT_NOT_FOUND.getMessage(),
                String.format(ExceptionEnum.STUDENT_NOT_FOUND.getLog(), id)
        ));
    }

    private static Specification<StudentEntity> likeIgnoreCase(String field, String value) {
        String pattern = contains(value);
        return (root, query, cb) -> cb.like(cb.lower(root.get(field)), pattern);
    }

    private static String contains(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }
}
