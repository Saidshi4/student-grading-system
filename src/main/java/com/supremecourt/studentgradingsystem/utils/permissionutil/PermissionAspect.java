package com.supremecourt.studentgradingsystem.utils.permissionutil;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.exception.PermissionException;
import com.supremecourt.studentgradingsystem.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final PermissionService permissionService;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        String role = jwtService.extractRolesFromToken(request);
        Long userId=jwtService.extractUserIdFromAccessToken(request);
        String requiredPermission = requiresPermission.value();
        boolean hasPermission = permissionService.hasPermission(role, requiredPermission);

        if (!hasPermission) {
            throw new PermissionException("Sizə bu funksionallığı istifadə etmək üçün icazə verilməmişdir!",
                    String.format("ActionLog.checkPermission.error by userId %d",userId));
        }
    }
}