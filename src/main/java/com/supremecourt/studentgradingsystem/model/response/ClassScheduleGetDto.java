package com.supremecourt.studentgradingsystem.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassScheduleGetDto {
    private Long id;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private Long courseOfferingId;
}
