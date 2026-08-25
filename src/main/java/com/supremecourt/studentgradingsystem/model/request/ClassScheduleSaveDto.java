package com.supremecourt.studentgradingsystem.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassScheduleSaveDto {
    @NotNull
    private DayOfWeek day;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    private String room;
    @NotNull
    private Long courseOfferingId;
}
