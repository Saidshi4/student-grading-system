package com.supremecourt.studentgradingsystem.service;

import com.supremecourt.studentgradingsystem.dao.entity.GradeEntity;
import com.supremecourt.studentgradingsystem.enums.GradeType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public final class FinalScoreCalculator {

    private static final Map<GradeType, BigDecimal> WEIGHTS = Map.of(
            GradeType.QUIZ, new BigDecimal("0.10"),
            GradeType.ASSIGNMENT, new BigDecimal("0.20"),
            GradeType.MIDTERM, new BigDecimal("0.30"),
            GradeType.FINAL, new BigDecimal("0.40")
    );

    private FinalScoreCalculator() {
    }

    public static Double calculate(Collection<GradeEntity> grades) {
        if (grades == null || grades.isEmpty()) {
            return null;
        }
        Map<GradeType, Integer> scores = new EnumMap<>(GradeType.class);
        for (GradeEntity grade : grades) {
            if (grade.getType() != null && grade.getScore() != null) {
                scores.putIfAbsent(grade.getType(), grade.getScore());
            }
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<GradeType, BigDecimal> weight : WEIGHTS.entrySet()) {
            int score = scores.getOrDefault(weight.getKey(), 0);
            total = total.add(BigDecimal.valueOf(score).multiply(weight.getValue()));
        }
        return total.setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
