package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.response.UserGetDto;
import com.supremecourt.studentgradingsystem.model.response.UserResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity mapUserRegistrationDtoToEntity(UserRegistrationDto dto);
    UserResponseDto mapUserEntityToResponseDto(UserEntity user);

    UserGetDto mapEntityToGetDto(UserEntity updatedUser);
    List<UserGetDto> mapEntityListToGetDtoList(List<UserEntity> userList);
}
