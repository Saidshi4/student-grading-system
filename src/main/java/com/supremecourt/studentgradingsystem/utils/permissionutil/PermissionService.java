package com.supremecourt.studentgradingsystem.utils.permissionutil;

import com.supremecourt.studentgradingsystem.dao.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final ClaimRepository claimRepository;

    public boolean hasPermission(String role, String permission) {
        List<String> claims=claimRepository.findClaimNamesByRoleName(role);
        return claims.contains(permission);
    }
}
