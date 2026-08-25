package com.supremecourt.studentgradingsystem.utils;

import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotPermission;
import com.supremecourt.studentgradingsystem.exception.UserNotAuthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity user)) {
            throw new UserNotAuthorizedException(
                    ExceptionEnum.USER_NOT_AUTHORIZED.getMessage(),
                    "No authenticated user in security context"
            );
        }
        return user;
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static String getCurrentRoleName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String fromAuth = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(name -> name != null && !name.isBlank())
                    .findFirst()
                    .orElse(null);
            if (fromAuth != null) {
                return fromAuth;
            }
        }
        UserEntity user = getCurrentUser();
        return user.getRole() != null ? user.getRole().getName() : null;
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentRoleName());
    }

    public static boolean isTeacher() {
        return "TEACHER".equals(getCurrentRoleName());
    }

    public static boolean isStudent() {
        return "STUDENT".equals(getCurrentRoleName());
    }

    public static void ensureStudentOwnsResource(Long ownerStudentId) {
        if (isStudent() && !getCurrentUserId().equals(ownerStudentId)) {
            deny();
        }
    }

    public static void ensureTeacherOwnsCourse(Long courseTeacherId) {
        if (isTeacher() && !getCurrentUserId().equals(courseTeacherId)) {
            deny();
        }
    }

    public static void deny() {
        throw new NotPermission(
                ExceptionEnum.NOT_PERMITTED.getMessage(),
                String.format(ExceptionEnum.NOT_PERMITTED.getLog(), getCurrentUserId())
        );
    }
}
