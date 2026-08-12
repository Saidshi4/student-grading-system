package com.supremecourt.studentgradingsystem.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class MenuResponseDto {
     Long id;
     String name;
     String icon;
     String path;
     Boolean isVisible;
     Instant createdAt;
     Instant updatedAt;
     List<ComponentResponseDto> componentResponseDtos;
}
