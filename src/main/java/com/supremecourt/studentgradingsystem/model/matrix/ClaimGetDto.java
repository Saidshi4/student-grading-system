package com.supremecourt.studentgradingsystem.model.matrix;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class ClaimGetDto {
    Long id;
    String name;
    String displayName;
}
