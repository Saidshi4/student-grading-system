package com.supremecourt.studentgradingsystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    USER_NOT_FOUND("ActionLog.findById.error user %d not found"),
    ROLE_NOT_FOUND("ActionLog.findById.error role %s not found"),
    CLAIM_NOT_FOUND("ActionLog.findById.error claim %d not found"),
    USER_ALREADY_EXISTS("ActionLog.findUserByEmail.error user already exists with %s"),
    FILE_IS_EMPTY("ActionLog.findById.error file is empty"),
    FAILED_TO_DELETE_IMAGE("ActionLog.findById.error failed to delete image %s"),
    FAILED_TO_UPLOAD_IMAGE("ActionLog.findById.error failed to upload image %s"),
    USER_NOT_FOUND_BY_EMAIL("ActionLog.findByEmail.error user %s not found"),
    EMAIL_OR_PASSWORD_INCORRECT("ActionLog.authentication.error email or password incorrect"),
    USER_NOT_AUTHORIZED("ActionLog.unauthorized.error user not authorized"),
    JWT_TOKEN_EXPIRED("ActionLog.jwtExpired.error JWT token expired for user %s"),
    NOT_PERMITTED("ActionLog.notPermitted.error user %d does not have permission to perform this action"),
    ACCOUNT_DELETE_NOT_ALLOWED("ActionLog.deleteAccount.error account deletion not allowed for user %d role %s"),
    ACCOUNT_ALREADY_DELETED("ActionLog.deleteAccount.error account already deleted for user %d"),
    ACCOUNT_DELETE_PROTECTED("ActionLog.deleteAccount.error protected account cannot be deleted user %d");

    private final String log;
}
