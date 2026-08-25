package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.GroupEntity;
import com.supremecourt.studentgradingsystem.model.request.GroupSaveDto;
import com.supremecourt.studentgradingsystem.model.response.GroupGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupEntity mapSaveDtoToEntity(GroupSaveDto dto);

    void updateEntityFromDto(GroupSaveDto dto, @MappingTarget GroupEntity entity);

    GroupGetDto mapEntityToGetDto(GroupEntity entity);

    List<GroupGetDto> mapEntityToGetDtos(List<GroupEntity> entities);
}
