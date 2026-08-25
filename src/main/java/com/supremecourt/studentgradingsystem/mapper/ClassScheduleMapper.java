package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.ClassScheduleEntity;
import com.supremecourt.studentgradingsystem.model.response.ClassScheduleGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassScheduleMapper {
    @Mapping(target = "courseOfferingId", source = "courseOffering.id")
    ClassScheduleGetDto mapEntityToGetDto(ClassScheduleEntity entity);

    List<ClassScheduleGetDto> mapEntityToGetDtos(List<ClassScheduleEntity> entities);
}
