package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.SubjectSaveDto;
import com.supremecourt.studentgradingsystem.model.response.SubjectGetDto;
import com.supremecourt.studentgradingsystem.service.SubjectService;
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
@RequestMapping("/subjects")
public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("subject_create")
    public SubjectGetDto create(@Valid @RequestBody SubjectSaveDto dto) {
        return subjectService.create(dto);
    }

    @GetMapping
    public List<SubjectGetDto> getAll() {
        return subjectService.getAll();
    }

    @GetMapping("/{id}")
    public SubjectGetDto getById(@PathVariable Long id) {
        return subjectService.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("subject_update")
    public SubjectGetDto update(@PathVariable Long id, @Valid @RequestBody SubjectSaveDto dto) {
        return subjectService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("subject_delete")
    public void delete(@PathVariable Long id) {
        subjectService.delete(id);
    }
}
