package com.supremecourt.studentgradingsystem.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupGetDto {
    private Long id;
    private String name;
    private String code;
    private String program;
    private String year;
}
