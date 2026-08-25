package com.supremecourt.studentgradingsystem.model.request;

import com.supremecourt.studentgradingsystem.enums.GradeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeSaveDto {
    @NotNull
    @Min(0)
    @Max(100)
    private Integer score;
    @NotNull
    private GradeType type;
    @NotNull
    private Long enrollmentId;
}
