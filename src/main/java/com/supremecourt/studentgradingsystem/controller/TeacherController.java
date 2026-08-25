package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.TeacherSaveDto;
import com.supremecourt.studentgradingsystem.model.response.TeacherGetDto;
import com.supremecourt.studentgradingsystem.service.TeacherService;
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
@RequestMapping("/teachers")
public class TeacherController {
    private final TeacherService teacherService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("teacher_create")
    public TeacherGetDto create(@Valid @RequestBody TeacherSaveDto dto) {
        return teacherService.create(dto);
    }

    @GetMapping
    public List<TeacherGetDto> getAll() {
        return teacherService.getAll();
    }

    @GetMapping("/{id}")
    public TeacherGetDto getById(@PathVariable Long id) {
        return teacherService.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("teacher_update")
    public TeacherGetDto update(@PathVariable Long id, @Valid @RequestBody TeacherSaveDto dto) {
        return teacherService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("teacher_delete")
    public void delete(@PathVariable Long id) {
        teacherService.delete(id);
    }
}
