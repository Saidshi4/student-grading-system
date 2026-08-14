package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.ClaimEntity;
import com.supremecourt.studentgradingsystem.model.matrix.ClaimGetDto;
import com.supremecourt.studentgradingsystem.model.request.ClaimSaveDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimMapper {
    ClaimGetDto mapEntityToGetDto(ClaimEntity claimEntity);
    List<ClaimGetDto> mapEntityToGetDtos(List<ClaimEntity> claimsEntities);
    ClaimEntity mapSaveDtoToEntity(ClaimSaveDto claimSaveDto);
}
