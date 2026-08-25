package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.GroupEntity;
import com.supremecourt.studentgradingsystem.dao.repository.GroupRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.IsNotEmptyException;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.GroupMapper;
import com.supremecourt.studentgradingsystem.model.request.GroupSaveDto;
import com.supremecourt.studentgradingsystem.model.response.GroupGetDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public GroupGetDto create(GroupSaveDto dto) {
        log.info("ActionLog.createGroup.start");
        GroupGetDto result = groupMapper.mapEntityToGetDto(groupRepository.save(groupMapper.mapSaveDtoToEntity(dto)));
        log.info("ActionLog.createGroup.end");
        return result;
    }

    public List<GroupGetDto> getAll() {
        log.info("ActionLog.getAllGroups.start");
        List<GroupGetDto> result = groupMapper.mapEntityToGetDtos(groupRepository.findAll());
        log.info("ActionLog.getAllGroups.end");
        return result;
    }

    public GroupGetDto getById(Long id) {
        log.info("ActionLog.getGroupById.start id {}", id);
        GroupGetDto result = groupMapper.mapEntityToGetDto(findEntity(id));
        log.info("ActionLog.getGroupById.end id {}", id);
        return result;
    }

    @Transactional
    public GroupGetDto update(Long id, GroupSaveDto dto) {
        log.info("ActionLog.updateGroup.start id {}", id);
        GroupEntity entity = findEntity(id);
        groupMapper.updateEntityFromDto(dto, entity);
        GroupGetDto result = groupMapper.mapEntityToGetDto(groupRepository.save(entity));
        log.info("ActionLog.updateGroup.end id {}", id);
        return result;
    }

    @Transactional
    public void delete(Long id) {
        log.info("ActionLog.deleteGroup.start id {}", id);
        GroupEntity entity = findEntity(id);
        if (entity.getStudents() != null && !entity.getStudents().isEmpty()) {
            throw new IsNotEmptyException("Group has students and cannot be deleted",
                    "ActionLog.deleteGroup.error group " + id + " has students");
        }
        if (entity.getCourseOfferings() != null && !entity.getCourseOfferings().isEmpty()) {
            throw new IsNotEmptyException("Group has course offerings and cannot be deleted",
                    "ActionLog.deleteGroup.error group " + id + " has course offerings");
        }
        groupRepository.delete(entity);
        log.info("ActionLog.deleteGroup.end id {}", id);
    }

    public GroupEntity findEntity(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new NotFoundException(
                ExceptionEnum.GROUP_NOT_FOUND.getMessage(),
                String.format(ExceptionEnum.GROUP_NOT_FOUND.getLog(), id)
        ));
    }
}
