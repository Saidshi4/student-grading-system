package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.CourseOfferingEntity;
import com.supremecourt.studentgradingsystem.model.response.CourseOfferingGetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseOfferingMapper {
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherUsername", source = "teacher.username")
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "academicYear", source = "semester.academicYear")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    CourseOfferingGetDto mapEntityToGetDto(CourseOfferingEntity entity);

    List<CourseOfferingGetDto> mapEntityToGetDtos(List<CourseOfferingEntity> entities);
}
