package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.GradeEntity;
import com.supremecourt.studentgradingsystem.model.response.GradeGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    @Mapping(target = "enrollmentId", source = "enrollment.id")
    @Mapping(target = "studentUsername", source = "enrollment.student.username")
    GradeGetDto mapEntityToGetDto(GradeEntity entity);

    List<GradeGetDto> mapEntityToGetDtos(List<GradeEntity> entities);
}
