package com.supremecourt.studentgradingsystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    USER_NOT_FOUND("User not found", "ActionLog.findById.error user %d not found"),
    ROLE_NOT_FOUND("Role not found", "ActionLog.findById.error role %s not found"),
    CLAIM_NOT_FOUND("Claim not found", "ActionLog.findById.error claim %d not found"),
    USER_ALREADY_EXISTS("User already exists", "ActionLog.findUserByEmail.error user already exists with %s"),
    FILE_IS_EMPTY("File is empty", "ActionLog.findById.error file is empty"),
    FAILED_TO_DELETE_IMAGE("Failed to delete image", "ActionLog.findById.error failed to delete image %s"),
    FAILED_TO_UPLOAD_IMAGE("Failed to upload image", "ActionLog.findById.error failed to upload image %s"),
    USER_NOT_FOUND_BY_EMAIL("User not found", "ActionLog.findByEmail.error user %s not found"),
    USERNAME_OR_PASSWORD_INCORRECT("Username or password incorrect", "ActionLog.authentication.error username or password incorrect"),
    USER_NOT_AUTHORIZED("User not authorized", "ActionLog.unauthorized.error user not authorized"),
    JWT_TOKEN_EXPIRED("JWT token expired", "ActionLog.jwtExpired.error JWT token expired for user %s"),
    NOT_PERMITTED("You are not allowed to access this resource", "ActionLog.notPermitted.error user %d does not have permission to perform this action"),
    ACCOUNT_DELETE_NOT_ALLOWED("Account deletion is not allowed", "ActionLog.deleteAccount.error account deletion not allowed for user %d role %s"),
    ACCOUNT_ALREADY_DELETED("Account already deleted", "ActionLog.deleteAccount.error account already deleted for user %d"),
    ACCOUNT_DELETE_PROTECTED("Protected account cannot be deleted", "ActionLog.deleteAccount.error protected account cannot be deleted user %d"),
    GROUP_NOT_FOUND("Group not found", "ActionLog.findById.error group %d not found"),
    SUBJECT_NOT_FOUND("Subject not found", "ActionLog.findById.error subject %d not found"),
    SEMESTER_NOT_FOUND("Semester not found", "ActionLog.findById.error semester %d not found"),
    COURSE_OFFERING_NOT_FOUND("Course offering not found", "ActionLog.findById.error course offering %d not found"),
    ENROLLMENT_NOT_FOUND("Enrollment not found", "ActionLog.findById.error enrollment %d not found"),
    GRADE_NOT_FOUND("Grade not found", "ActionLog.findById.error grade %d not found"),
    STUDENT_NOT_FOUND("Student not found", "ActionLog.findById.error student %d not found"),
    TEACHER_NOT_FOUND("Teacher not found", "ActionLog.findById.error teacher %d not found"),
    CLASS_SCHEDULE_NOT_FOUND("Class schedule not found", "ActionLog.findById.error class schedule %d not found"),
    ENROLLMENT_ALREADY_EXISTS("Student is already enrolled in this course offering", "ActionLog.createEnrollment.error student already enrolled in course offering"),
    GRADE_ALREADY_EXISTS("Grade type already exists for this enrollment", "ActionLog.createGrade.error grade type already exists for enrollment"),
    SUBJECT_CODE_ALREADY_EXISTS("Subject code already exists", "ActionLog.createSubject.error subject code already exists %s"),
    COURSE_OFFERING_FULL("Course offering is at full capacity", "ActionLog.createEnrollment.error course offering %d is full"),
    INVALID_DATE_RANGE("Start date must be before end date", "ActionLog.validateDates.error startDate must be before endDate"),
    INVALID_TIME_RANGE("Start time must be before end time", "ActionLog.validateTimes.error startTime must be before endTime");

    private final String message;
    private final String log;
}
