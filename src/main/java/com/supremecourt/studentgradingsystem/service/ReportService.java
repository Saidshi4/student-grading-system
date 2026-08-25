package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.StudentEntity;
import com.supremecourt.studentgradingsystem.dao.repository.EnrollmentRepository;
import com.supremecourt.studentgradingsystem.enums.EnrollmentStatus;
import com.supremecourt.studentgradingsystem.model.response.CourseAverageScoreDto;
import com.supremecourt.studentgradingsystem.model.response.StudentAverageScoreDto;
import com.supremecourt.studentgradingsystem.model.response.TopStudentDto;
import com.supremecourt.studentgradingsystem.model.response.TranscriptGetDto;
import com.supremecourt.studentgradingsystem.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final CourseOfferingService courseOfferingService;

    @Transactional(readOnly = true)
    public TranscriptGetDto getTranscript(Long studentId) {
        log.info("ActionLog.getTranscript.start studentId {}", studentId);
        StudentEntity student = studentService.findEntity(studentId);
        assertCanAccessStudent(studentId);
        List<TranscriptGetDto.TranscriptCourseDto> courses = enrollmentRepository.findByStudentIdWithGrades(studentId)
                .stream()
                .filter(enrollment -> enrollment.getStatus() != EnrollmentStatus.DROPPED)
                .map(enrollment -> TranscriptGetDto.TranscriptCourseDto.builder()
                        .subject(enrollment.getCourseOffering().getSubject().getName())
                        .credit(enrollment.getCourseOffering().getSubject().getCredits())
                        .finalScore(FinalScoreCalculator.calculate(enrollment.getGrades()))
                        .build())
                .toList();
        TranscriptGetDto result = TranscriptGetDto.builder()
                .studentId(student.getId())
                .studentName(fullName(student))
                .courses(courses)
                .build();
        log.info("ActionLog.getTranscript.end studentId {}", studentId);
        return result;
    }

    @Transactional(readOnly = true)
    public CourseAverageScoreDto getCourseAverageScore(Long courseId) {
        log.info("ActionLog.getCourseAverageScore.start courseId {}", courseId);
        assertCanAccessCourse(courseId);
        List<Double> scores = scoredEnrollments(courseId);
        Double average = scores.isEmpty() ? null : round(scores.stream().mapToDouble(Double::doubleValue).average().orElse(0));
        log.info("ActionLog.getCourseAverageScore.end courseId {}", courseId);
        return CourseAverageScoreDto.builder()
                .courseId(courseId)
                .averageScore(average)
                .build();
    }

    @Transactional(readOnly = true)
    public List<TopStudentDto> getTopStudents(Long courseId) {
        log.info("ActionLog.getTopStudents.start courseId {}", courseId);
        assertCanAccessCourse(courseId);
        List<TopStudentDto> result = enrollmentRepository.findByCourseOfferingIdWithGrades(courseId).stream()
                .filter(enrollment -> enrollment.getStatus() != EnrollmentStatus.DROPPED)
                .map(enrollment -> {
                    Double finalScore = FinalScoreCalculator.calculate(enrollment.getGrades());
                    if (finalScore == null) {
                        return null;
                    }
                    StudentEntity student = enrollment.getStudent();
                    return TopStudentDto.builder()
                            .studentId(student.getId())
                            .studentName(fullName(student))
                            .username(student.getUsername())
                            .finalScore(finalScore)
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(TopStudentDto::getFinalScore).reversed())
                .limit(5)
                .toList();
        log.info("ActionLog.getTopStudents.end courseId {}", courseId);
        return result;
    }

    @Transactional(readOnly = true)
    public StudentAverageScoreDto getStudentAverageScore(Long studentId) {
        log.info("ActionLog.getStudentAverageScore.start studentId {}", studentId);
        studentService.findEntity(studentId);
        assertCanAccessStudent(studentId);
        List<Double> scores = enrollmentRepository.findByStudentIdWithGrades(studentId).stream()
                .filter(enrollment -> enrollment.getStatus() != EnrollmentStatus.DROPPED)
                .map(enrollment -> FinalScoreCalculator.calculate(enrollment.getGrades()))
                .filter(Objects::nonNull)
                .toList();
        Double average = scores.isEmpty() ? null : round(scores.stream().mapToDouble(Double::doubleValue).average().orElse(0));
        log.info("ActionLog.getStudentAverageScore.end studentId {}", studentId);
        return StudentAverageScoreDto.builder()
                .studentId(studentId)
                .averageScore(average)
                .build();
    }

    private List<Double> scoredEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseOfferingIdWithGrades(courseId).stream()
                .filter(enrollment -> enrollment.getStatus() != EnrollmentStatus.DROPPED)
                .map(enrollment -> FinalScoreCalculator.calculate(enrollment.getGrades()))
                .filter(Objects::nonNull)
                .toList();
    }

    private void assertCanAccessCourse(Long courseId) {
        var offering = courseOfferingService.findEntity(courseId);
        SecurityUtils.ensureTeacherOwnsCourse(offering.getTeacher().getId());
        if (SecurityUtils.isStudent()) {
            SecurityUtils.deny();
        }
    }

    private void assertCanAccessStudent(Long studentId) {
        SecurityUtils.ensureStudentOwnsResource(studentId);
        if (SecurityUtils.isTeacher()
                && !enrollmentRepository.existsByStudentIdAndCourseOfferingTeacherId(studentId, SecurityUtils.getCurrentUserId())) {
            SecurityUtils.deny();
        }
    }

    private static String fullName(StudentEntity student) {
        return ((student.getFirstName() == null ? "" : student.getFirstName()) + " "
                + (student.getLastName() == null ? "" : student.getLastName())).trim();
    }

    private static Double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
