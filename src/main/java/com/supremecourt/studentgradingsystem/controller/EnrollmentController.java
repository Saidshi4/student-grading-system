package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.EnrollmentSaveDto;
import com.supremecourt.studentgradingsystem.model.response.EnrollmentGetDto;
import com.supremecourt.studentgradingsystem.service.EnrollmentService;
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
@RequestMapping("/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("enrollment_create")
    public EnrollmentGetDto create(@Valid @RequestBody EnrollmentSaveDto dto) {
        return enrollmentService.create(dto);
    }

    @GetMapping
    public List<EnrollmentGetDto> getAll(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseOfferingId) {
        return enrollmentService.getAll(studentId, courseOfferingId);
    }

    @GetMapping("/{id}")
    public EnrollmentGetDto getById(@PathVariable Long id) {
        return enrollmentService.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("enrollment_update")
    public EnrollmentGetDto update(@PathVariable Long id, @Valid @RequestBody EnrollmentSaveDto dto) {
        return enrollmentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("enrollment_delete")
    public void delete(@PathVariable Long id) {
        enrollmentService.delete(id);
    }
}
