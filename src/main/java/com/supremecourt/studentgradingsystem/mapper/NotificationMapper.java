package com.supremecourt.studentgradingsystem.mapper;

import com.supremecourt.studentgradingsystem.dao.entity.NotificationEntity;
import com.supremecourt.studentgradingsystem.model.response.NotificationResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponseDto mapToDto(NotificationEntity notificationEntity);
    List<NotificationResponseDto> mapToDtos(List<NotificationEntity> notificationEntities);
}
