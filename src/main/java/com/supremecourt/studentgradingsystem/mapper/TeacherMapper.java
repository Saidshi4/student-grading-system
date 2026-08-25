package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.TeacherEntity;
import com.supremecourt.studentgradingsystem.model.response.TeacherGetDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    TeacherGetDto mapEntityToGetDto(TeacherEntity entity);

    List<TeacherGetDto> mapEntityToGetDtos(List<TeacherEntity> entities);
}
