package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.model.matrix.RoleGetDto;
import com.supremecourt.studentgradingsystem.model.request.RoleSaveDto;
import com.supremecourt.studentgradingsystem.model.request.RoleUpdateDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
      RoleEntity mapSaveDtoToEntity(RoleSaveDto roleSaveDto);
      RoleGetDto mapEntityToGetDto(RoleEntity roleEntity);
      List<RoleGetDto> mapEntityToGetDtos(List<RoleEntity> roleEntity);
      RoleEntity mapUpdateDtoToEntity(RoleUpdateDto roleUpdateDto);
}
