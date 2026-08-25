package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.StudentEntity;
import com.supremecourt.studentgradingsystem.model.response.StudentGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    StudentGetDto mapEntityToGetDto(StudentEntity entity);

    List<StudentGetDto> mapEntityToGetDtos(List<StudentEntity> entities);
}
