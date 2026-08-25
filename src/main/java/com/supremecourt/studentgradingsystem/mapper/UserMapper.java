package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.request.UserUpdateDto;
import com.supremecourt.studentgradingsystem.model.response.UserGetDto;
import com.supremecourt.studentgradingsystem.model.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    void mapUserRegistrationDtoToEntity(UserRegistrationDto dto, @MappingTarget UserEntity entity);

    @Mapping(target = "role", ignore = true)
    UserEntity mapUserRegistrationDtoToEntity(UserRegistrationDto dto);

    UserResponseDto mapUserEntityToResponseDto(UserEntity user);

    @Mapping(target = "fullName", expression = "java(updatedUser.getFirstName() + \" \" + updatedUser.getLastName())")
    @Mapping(target = "role", source = "role.name")
    UserGetDto mapEntityToGetDto(UserEntity updatedUser);

    List<UserGetDto> mapEntityListToGetDtoList(List<UserEntity> userList);

    void updateEntityFromDto(UserUpdateDto dto, @MappingTarget UserEntity entity);
}
