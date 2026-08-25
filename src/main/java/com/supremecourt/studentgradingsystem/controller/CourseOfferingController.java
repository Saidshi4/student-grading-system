package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.CourseOfferingSaveDto;
import com.supremecourt.studentgradingsystem.model.response.CourseOfferingGetDto;
import com.supremecourt.studentgradingsystem.service.CourseOfferingService;
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
@RequestMapping("/course-offerings")
public class CourseOfferingController {
    private final CourseOfferingService courseOfferingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("course_offering_create")
    public CourseOfferingGetDto create(@Valid @RequestBody CourseOfferingSaveDto dto) {
        return courseOfferingService.create(dto);
    }

    @GetMapping
    public List<CourseOfferingGetDto> getAll(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long groupId) {
        return courseOfferingService.getAll(teacherId, semesterId, groupId);
    }

    @GetMapping("/{id}")
    public CourseOfferingGetDto getById(@PathVariable Long id) {
        return courseOfferingService.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("course_offering_update")
    public CourseOfferingGetDto update(@PathVariable Long id, @Valid @RequestBody CourseOfferingSaveDto dto) {
        return courseOfferingService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("course_offering_delete")
    public void delete(@PathVariable Long id) {
        courseOfferingService.delete(id);
    }
}
