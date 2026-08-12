package com.supremecourt.studentgradingsystem.model.request.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStaffRequestDto {
    private String position;
    private String about;
    private String experience;
    private Long branchId;
}
