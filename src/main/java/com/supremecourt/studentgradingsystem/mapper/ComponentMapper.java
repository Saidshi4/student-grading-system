package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.ComponentEntity;
import com.supremecourt.studentgradingsystem.model.request.ComponentSaveDto;
import com.supremecourt.studentgradingsystem.model.response.ComponentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComponentMapper {
    ComponentResponseDto mapComponentEntityToResponseDto(ComponentEntity componentEntity);
    List<ComponentResponseDto> mapComponentEntityToResponseDtos(List<ComponentEntity> componentEntities);
    @Mapping(target="claims.id",source = "claimId")
    @Mapping(target="menu.id",source = "menuId")
    ComponentEntity mapSaveDtoToEntity(ComponentSaveDto componentSaveDto);
}
