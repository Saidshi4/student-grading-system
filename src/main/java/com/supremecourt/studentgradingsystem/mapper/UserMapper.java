package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.response.UserGetDto;
import com.supremecourt.studentgradingsystem.model.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role", ignore = true)
    UserEntity mapUserRegistrationDtoToEntity(UserRegistrationDto dto);
    UserResponseDto mapUserEntityToResponseDto(UserEntity user);

    UserGetDto mapEntityToGetDto(UserEntity updatedUser);
    List<UserGetDto> mapEntityListToGetDtoList(List<UserEntity> userList);
}
