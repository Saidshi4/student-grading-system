package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.ClassScheduleSaveDto;
import com.supremecourt.studentgradingsystem.model.response.ClassScheduleGetDto;
import com.supremecourt.studentgradingsystem.service.ClassScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/class-schedules")
public class ClassScheduleController {
    private final ClassScheduleService classScheduleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("class_schedule_create")
    public ClassScheduleGetDto create(@Valid @RequestBody ClassScheduleSaveDto dto) {
        return classScheduleService.create(dto);
    }

    @GetMapping
    public List<ClassScheduleGetDto> getAll(@RequestParam(required = false) Long courseOfferingId) {
        return classScheduleService.getAll(courseOfferingId);
    }

    @GetMapping("/{id}")
    public ClassScheduleGetDto getById(@PathVariable Long id) {
        return classScheduleService.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("class_schedule_update")
    public ClassScheduleGetDto update(@PathVariable Long id, @Valid @RequestBody ClassScheduleSaveDto dto) {
        return classScheduleService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("class_schedule_delete")
    public void delete(@PathVariable Long id) {
        classScheduleService.delete(id);
    }
}
