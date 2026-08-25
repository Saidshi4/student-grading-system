package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.GradeSaveDto;
import com.supremecourt.studentgradingsystem.model.response.GradeGetDto;
import com.supremecourt.studentgradingsystem.service.GradeService;
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
@RequestMapping("/grades")
public class GradeController {
    private final GradeService gradeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("grade_create")
    public GradeGetDto create(@Valid @RequestBody GradeSaveDto dto) {
        return gradeService.create(dto);
    }

    @GetMapping
    public List<GradeGetDto> getAll(@RequestParam(required = false) Long enrollmentId) {
        return gradeService.getAll(enrollmentId);
    }

    @GetMapping("/{id}")
    public GradeGetDto getById(@PathVariable Long id) {
        return gradeService.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("grade_update")
    public GradeGetDto update(@PathVariable Long id, @Valid @RequestBody GradeSaveDto dto) {
        return gradeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("grade_delete")
    public void delete(@PathVariable Long id) {
        gradeService.delete(id);
    }
}
