package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.SemesterEntity;
import com.supremecourt.studentgradingsystem.model.request.SemesterSaveDto;
import com.supremecourt.studentgradingsystem.model.response.SemesterGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SemesterMapper {
    SemesterEntity mapSaveDtoToEntity(SemesterSaveDto dto);

    void updateEntityFromDto(SemesterSaveDto dto, @MappingTarget SemesterEntity entity);

    SemesterGetDto mapEntityToGetDto(SemesterEntity entity);

    List<SemesterGetDto> mapEntityToGetDtos(List<SemesterEntity> entities);
}
