package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.model.response.CourseAverageScoreDto;
import com.supremecourt.studentgradingsystem.model.response.StudentAverageScoreDto;
import com.supremecourt.studentgradingsystem.model.response.TopStudentDto;
import com.supremecourt.studentgradingsystem.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/courses/{courseId}/average-score")
    public CourseAverageScoreDto getCourseAverageScore(@PathVariable Long courseId) {
        return reportService.getCourseAverageScore(courseId);
    }

    @GetMapping("/courses/{courseId}/top-students")
    public List<TopStudentDto> getTopStudents(@PathVariable Long courseId) {
        return reportService.getTopStudents(courseId);
    }

    @GetMapping("/students/{studentId}/average-score")
    public StudentAverageScoreDto getStudentAverageScore(@PathVariable Long studentId) {
        return reportService.getStudentAverageScore(studentId);
    }
}
