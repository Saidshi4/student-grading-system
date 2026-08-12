package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.MenuEntity;
import com.supremecourt.studentgradingsystem.model.request.MenuSaveDto;
import com.supremecourt.studentgradingsystem.model.response.MenuResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses={ComponentMapper.class})
public interface MenuMapper {
    @Mapping(target="componentResponseDtos", source = "components")
    MenuResponseDto mapMenuEntityToResponseDto(MenuEntity menuEntity);
    List<MenuResponseDto> mapMenuEntityToResponseDtos(List<MenuEntity> menuEntities);
    @Mapping(target = "claims.id",source = "claimId")
    MenuEntity mapSaveDtoToEntity(MenuSaveDto menuSaveDto);
}
