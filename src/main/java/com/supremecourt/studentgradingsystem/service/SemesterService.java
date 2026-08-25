package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.SemesterEntity;
import com.supremecourt.studentgradingsystem.dao.repository.SemesterRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.IsNotEmptyException;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.SemesterMapper;
import com.supremecourt.studentgradingsystem.model.request.SemesterSaveDto;
import com.supremecourt.studentgradingsystem.model.response.SemesterGetDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SemesterService {
    private final SemesterRepository semesterRepository;
    private final SemesterMapper semesterMapper;

    public SemesterGetDto create(SemesterSaveDto dto) {
        log.info("ActionLog.createSemester.start");
        validateDates(dto);
        SemesterGetDto result = semesterMapper.mapEntityToGetDto(semesterRepository.save(semesterMapper.mapSaveDtoToEntity(dto)));
        log.info("ActionLog.createSemester.end");
        return result;
    }

    public List<SemesterGetDto> getAll() {
        log.info("ActionLog.getAllSemesters.start");
        List<SemesterGetDto> result = semesterMapper.mapEntityToGetDtos(semesterRepository.findAll());
        log.info("ActionLog.getAllSemesters.end");
        return result;
    }

    public SemesterGetDto getById(Long id) {
        log.info("ActionLog.getSemesterById.start id {}", id);
        SemesterGetDto result = semesterMapper.mapEntityToGetDto(findEntity(id));
        log.info("ActionLog.getSemesterById.end id {}", id);
        return result;
    }

    @Transactional
    public SemesterGetDto update(Long id, SemesterSaveDto dto) {
        log.info("ActionLog.updateSemester.start id {}", id);
        SemesterEntity entity = findEntity(id);
        validateDates(dto);
        semesterMapper.updateEntityFromDto(dto, entity);
        SemesterGetDto result = semesterMapper.mapEntityToGetDto(semesterRepository.save(entity));
        log.info("ActionLog.updateSemester.end id {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteSemester.start id {}", id);
        SemesterEntity entity = findEntity(id);
        if (entity.getCourseOfferings() != null && !entity.getCourseOfferings().isEmpty()) {
            throw new IsNotEmptyException("Semester has course offerings and cannot be deleted",
                    "ActionLog.deleteSemester.error semester " + id + " has course offerings");
        }
        semesterRepository.delete(entity);
        log.info("ActionLog.deleteSemester.end id {}", id);
    }

    public SemesterEntity findEntity(Long id) {
        return semesterRepository.findById(id).orElseThrow(() -> new NotFoundException(
                ExceptionEnum.SEMESTER_NOT_FOUND.getMessage(),
                String.format(ExceptionEnum.SEMESTER_NOT_FOUND.getLog(), id)
        ));
    }

    private void validateDates(SemesterSaveDto dto) {
        if (dto.getStartDate() != null && dto.getEndDate() != null && !dto.getStartDate().isBefore(dto.getEndDate())) {
            throw new IllegalArgumentException(ExceptionEnum.INVALID_DATE_RANGE.getMessage());
        }
    }
}
