CREATE TABLE IF NOT EXISTS class_schedules (
    id BIGSERIAL PRIMARY KEY,
    day VARCHAR(32) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room VARCHAR(255),
    course_offering_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_class_schedules_course_offering_id
    ON class_schedules (course_offering_id);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'enrollments'
    ) THEN
        ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS status VARCHAR(32);
        ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS enrolled_at TIMESTAMP WITH TIME ZONE;
        ALTER TABLE enrollments ADD COLUMN IF NOT EXISTS dropped_at TIMESTAMP WITH TIME ZONE;

        UPDATE enrollments SET status = 'ENROLLED' WHERE status IS NULL;
        UPDATE enrollments SET enrolled_at = COALESCE(created_at, NOW()) WHERE enrolled_at IS NULL;

        CREATE UNIQUE INDEX IF NOT EXISTS uk_enrollment_student_course
            ON enrollments (student_id, course_offering_id);
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'grades'
    ) THEN
        ALTER TABLE grades ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);
        ALTER TABLE grades ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'subjects'
    ) THEN
        CREATE UNIQUE INDEX IF NOT EXISTS uk_subjects_code ON subjects (code);
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'roles_claims'
    ) THEN
        CREATE UNIQUE INDEX IF NOT EXISTS uk_roles_claims_role_claim
            ON roles_claims (roles_id, claims_id);
    END IF;
END $$;
