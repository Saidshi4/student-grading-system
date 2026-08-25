package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.SemesterSaveDto;
import com.supremecourt.studentgradingsystem.model.response.SemesterGetDto;
import com.supremecourt.studentgradingsystem.service.SemesterService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/semesters")
public class SemesterController {
    private final SemesterService semesterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("semester_create")
    public SemesterGetDto create(@Valid @RequestBody SemesterSaveDto dto) {
        return semesterService.create(dto);
    }

    @GetMapping
    public List<SemesterGetDto> getAll() {
        return semesterService.getAll();
    }

    @GetMapping("/{id}")
    public SemesterGetDto getById(@PathVariable Long id) {
        return semesterService.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("semester_update")
    public SemesterGetDto update(@PathVariable Long id, @Valid @RequestBody SemesterSaveDto dto) {
        return semesterService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("semester_delete")
    public void delete(@PathVariable Long id) {
        semesterService.delete(id);
    }
}
