package com.supremecourt.studentgradingsystem.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptGetDto {
    private Long studentId;
    private String studentName;
    private List<TranscriptCourseDto> courses;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranscriptCourseDto {
        private String subject;
        private Long credit;
        private Double finalScore;
    }
}
