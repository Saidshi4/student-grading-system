package com.supremecourt.studentgradingsystem.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class ComponentResponseDto {
     Long id;
     String name;
     Instant createdAt;
     Instant updatedAt;
}
