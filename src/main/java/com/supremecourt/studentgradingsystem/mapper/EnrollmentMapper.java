package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.EnrollmentEntity;
import com.supremecourt.studentgradingsystem.model.response.EnrollmentGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentUsername", source = "student.username")
    @Mapping(target = "courseOfferingId", source = "courseOffering.id")
    @Mapping(target = "subjectName", source = "courseOffering.subject.name")
    @Mapping(target = "finalScore", ignore = true)
    EnrollmentGetDto mapEntityToGetDto(EnrollmentEntity entity);

    List<EnrollmentGetDto> mapEntityToGetDtos(List<EnrollmentEntity> entities);
}
