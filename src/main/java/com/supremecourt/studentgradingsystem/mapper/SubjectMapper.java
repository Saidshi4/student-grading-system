package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.SubjectEntity;
import com.supremecourt.studentgradingsystem.model.request.SubjectSaveDto;
import com.supremecourt.studentgradingsystem.model.response.SubjectGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    SubjectEntity mapSaveDtoToEntity(SubjectSaveDto dto);

    void updateEntityFromDto(SubjectSaveDto dto, @MappingTarget SubjectEntity entity);

    SubjectGetDto mapEntityToGetDto(SubjectEntity entity);

    List<SubjectGetDto> mapEntityToGetDtos(List<SubjectEntity> entities);
}
