package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.SubjectEntity;
import com.supremecourt.studentgradingsystem.dao.repository.SubjectRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.IsNotEmptyException;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.exception.UserAlreadyExistsException;
import com.supremecourt.studentgradingsystem.mapper.SubjectMapper;
import com.supremecourt.studentgradingsystem.model.request.SubjectSaveDto;
import com.supremecourt.studentgradingsystem.model.response.SubjectGetDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public SubjectGetDto create(SubjectSaveDto dto) {
        log.info("ActionLog.createSubject.start");
        ensureUniqueCode(dto.getCode(), null);
        SubjectGetDto result = subjectMapper.mapEntityToGetDto(subjectRepository.save(subjectMapper.mapSaveDtoToEntity(dto)));
        log.info("ActionLog.createSubject.end");
        return result;
    }

    public List<SubjectGetDto> getAll() {
        log.info("ActionLog.getAllSubjects.start");
        List<SubjectGetDto> result = subjectMapper.mapEntityToGetDtos(subjectRepository.findAll());
        log.info("ActionLog.getAllSubjects.end");
        return result;
    }

    public SubjectGetDto getById(Long id) {
        log.info("ActionLog.getSubjectById.start id {}", id);
        SubjectGetDto result = subjectMapper.mapEntityToGetDto(findEntity(id));
        log.info("ActionLog.getSubjectById.end id {}", id);
        return result;
    }

    @Transactional
    public SubjectGetDto update(Long id, SubjectSaveDto dto) {
        log.info("ActionLog.updateSubject.start id {}", id);
        SubjectEntity entity = findEntity(id);
        ensureUniqueCode(dto.getCode(), id);
        subjectMapper.updateEntityFromDto(dto, entity);
        SubjectGetDto result = subjectMapper.mapEntityToGetDto(subjectRepository.save(entity));
        log.info("ActionLog.updateSubject.end id {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteSubject.start id {}", id);
        SubjectEntity entity = findEntity(id);
        if (entity.getCourseOfferings() != null && !entity.getCourseOfferings().isEmpty()) {
            throw new IsNotEmptyException("Subject has course offerings and cannot be deleted",
                    "ActionLog.deleteSubject.error subject " + id + " has course offerings");
        }
        subjectRepository.delete(entity);
        log.info("ActionLog.deleteSubject.end id {}", id);
    }

    public SubjectEntity findEntity(Long id) {
        return subjectRepository.findById(id).orElseThrow(() -> new NotFoundException(
                ExceptionEnum.SUBJECT_NOT_FOUND.getMessage(),
                String.format(ExceptionEnum.SUBJECT_NOT_FOUND.getLog(), id)
        ));
    }

    private void ensureUniqueCode(String code, Long id) {
        boolean exists = id == null
                ? subjectRepository.existsByCodeIgnoreCase(code)
                : subjectRepository.existsByCodeIgnoreCaseAndIdNot(code, id);
        if (exists) {
            throw new UserAlreadyExistsException(
                    ExceptionEnum.SUBJECT_CODE_ALREADY_EXISTS.getMessage(),
                    String.format(ExceptionEnum.SUBJECT_CODE_ALREADY_EXISTS.getLog(), code)
            );
        }
    }
}
