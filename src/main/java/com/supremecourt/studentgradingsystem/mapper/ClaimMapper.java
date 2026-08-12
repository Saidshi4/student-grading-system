package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.ClaimsEntity;
import com.supremecourt.studentgradingsystem.model.matrix.ClaimGetDto;
import com.supremecourt.studentgradingsystem.model.request.ClaimSaveDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimMapper {
    ClaimGetDto mapEntityToGetDto(ClaimsEntity claimsEntity);
    List<ClaimGetDto> mapEntityToGetDtos(List<ClaimsEntity> claimsEntities);
    ClaimsEntity mapSaveDtoToEntity(ClaimSaveDto claimSaveDto);
}
