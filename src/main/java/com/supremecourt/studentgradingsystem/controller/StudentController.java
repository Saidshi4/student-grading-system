package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.StudentSaveDto;
import com.supremecourt.studentgradingsystem.model.response.StudentGetDto;
import com.supremecourt.studentgradingsystem.model.response.TranscriptGetDto;
import com.supremecourt.studentgradingsystem.service.ReportService;
import com.supremecourt.studentgradingsystem.service.StudentService;
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
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;
    private final ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("student_create")
    public StudentGetDto create(@Valid @RequestBody StudentSaveDto dto) {
        return studentService.create(dto);
    }

    @GetMapping
    public List<StudentGetDto> getAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long groupId) {
        return studentService.getAll(firstName, lastName, username, name, groupId);
    }

    @GetMapping("/{id}")
    public StudentGetDto getById(@PathVariable Long id) {
        return studentService.getById(id);
    }

    @GetMapping("/{id}/transcript")
    public TranscriptGetDto getTranscript(@PathVariable Long id) {
        return reportService.getTranscript(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("student_update")
    public StudentGetDto update(@PathVariable Long id, @Valid @RequestBody StudentSaveDto dto) {
        return studentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("student_delete")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}
