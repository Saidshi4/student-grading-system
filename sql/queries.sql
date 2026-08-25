-- Student Grading System — SQL tasks
-- Username is the student identifier (no separate student_no).
-- Student/Teacher rows share users.id via JOINED inheritance.

-- 1. All students with their groups
SELECT
    u.id,
    u.username,
    u.first_name,
    u.last_name,
    u.email,
    g.id AS group_id,
    g.name AS group_name,
    g.code AS group_code
FROM students s
JOIN users u ON u.id = s.id
LEFT JOIN groups g ON g.id = s.group_id
ORDER BY u.last_name, u.first_name;

-- 2. Courses taught by a given teacher
SELECT
    co.id AS course_offering_id,
    sub.code AS subject_code,
    sub.name AS subject_name,
    sem.academic_year,
    sem.semester_type,
    g.name AS group_name,
    co.capacity,
    co.status
FROM course_offerings co
JOIN subjects sub ON sub.id = co.subject_id
JOIN semesters sem ON sem.id = co.semester_id
LEFT JOIN groups g ON g.id = co.group_id
WHERE co.teacher_id = :teacherId
ORDER BY sem.academic_year, sub.name;

-- 3. All grades of a given student
SELECT
    u.username,
    sub.name AS subject,
    g.type AS grade_type,
    g.score,
    g.created_at,
    g.created_by,
    g.updated_at,
    g.updated_by
FROM grades g
JOIN enrollments e ON e.id = g.enrollment_id
JOIN students s ON s.id = e.student_id
JOIN users u ON u.id = s.id
JOIN course_offerings co ON co.id = e.course_offering_id
JOIN subjects sub ON sub.id = co.subject_id
WHERE e.student_id = :studentId
ORDER BY sub.name, g.type;

-- 4. Student count per subject
SELECT
    sub.id AS subject_id,
    sub.code,
    sub.name,
    COUNT(e.id) AS student_count
FROM subjects sub
JOIN course_offerings co ON co.subject_id = sub.id
LEFT JOIN enrollments e ON e.course_offering_id = co.id
    AND (e.status IS NULL OR e.status = 'ENROLLED')
GROUP BY sub.id, sub.code, sub.name
ORDER BY student_count DESC, sub.name;

-- 5. Average final score per subject
-- Weights: QUIZ 10%, ASSIGNMENT 20%, MIDTERM 30%, FINAL 40%
SELECT
    sub.id AS subject_id,
    sub.name,
    ROUND(AVG(
        COALESCE(quiz.score, 0) * 0.10
        + COALESCE(assignment.score, 0) * 0.20
        + COALESCE(midterm.score, 0) * 0.30
        + COALESCE(final_grade.score, 0) * 0.40
    )::numeric, 1) AS average_score
FROM subjects sub
JOIN course_offerings co ON co.subject_id = sub.id
JOIN enrollments e ON e.course_offering_id = co.id
    AND (e.status IS NULL OR e.status = 'ENROLLED')
LEFT JOIN grades quiz ON quiz.enrollment_id = e.id AND quiz.type = 'QUIZ'
LEFT JOIN grades assignment ON assignment.enrollment_id = e.id AND assignment.type = 'ASSIGNMENT'
LEFT JOIN grades midterm ON midterm.enrollment_id = e.id AND midterm.type = 'MIDTERM'
LEFT JOIN grades final_grade ON final_grade.enrollment_id = e.id AND final_grade.type = 'FINAL'
GROUP BY sub.id, sub.name
ORDER BY average_score DESC NULLS LAST;

-- 6. Top 5 students by average final score
SELECT
    u.id AS student_id,
    u.username,
    u.first_name,
    u.last_name,
    ROUND(AVG(
        COALESCE(quiz.score, 0) * 0.10
        + COALESCE(assignment.score, 0) * 0.20
        + COALESCE(midterm.score, 0) * 0.30
        + COALESCE(final_grade.score, 0) * 0.40
    )::numeric, 1) AS average_score
FROM students s
JOIN users u ON u.id = s.id
JOIN enrollments e ON e.student_id = s.id
    AND (e.status IS NULL OR e.status = 'ENROLLED')
LEFT JOIN grades quiz ON quiz.enrollment_id = e.id AND quiz.type = 'QUIZ'
LEFT JOIN grades assignment ON assignment.enrollment_id = e.id AND assignment.type = 'ASSIGNMENT'
LEFT JOIN grades midterm ON midterm.enrollment_id = e.id AND midterm.type = 'MIDTERM'
LEFT JOIN grades final_grade ON final_grade.enrollment_id = e.id AND final_grade.type = 'FINAL'
GROUP BY u.id, u.username, u.first_name, u.last_name
ORDER BY average_score DESC NULLS LAST
LIMIT 5;

-- 7. Students not enrolled in any course
SELECT
    u.id,
    u.username,
    u.first_name,
    u.last_name,
    u.email
FROM students s
JOIN users u ON u.id = s.id
LEFT JOIN enrollments e ON e.student_id = s.id
WHERE e.id IS NULL
ORDER BY u.username;

-- 8. Subject with the most students in a given semester
SELECT
    sub.id AS subject_id,
    sub.code,
    sub.name,
    COUNT(e.id) AS student_count
FROM subjects sub
JOIN course_offerings co ON co.subject_id = sub.id
JOIN enrollments e ON e.course_offering_id = co.id
    AND (e.status IS NULL OR e.status = 'ENROLLED')
WHERE co.semester_id = :semesterId
GROUP BY sub.id, sub.code, sub.name
ORDER BY student_count DESC, sub.name
LIMIT 1;
