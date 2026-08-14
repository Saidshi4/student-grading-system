package com.supremecourt.studentgradingsystem.controller.permission;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.matrix.MatrixDto;
import com.supremecourt.studentgradingsystem.model.matrix.RoleClaimRequest;
import com.supremecourt.studentgradingsystem.service.auth.JwtService;
import com.supremecourt.studentgradingsystem.service.permission.RoleClaimService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role-claim")
@RequiredArgsConstructor
public class RoleClaimController {
    private final RoleClaimService roleClaimService;
    private final JwtService jwtService;

    @GetMapping("/matrix")
    @RequiresPermission("role_claim_read_matrix")
    public MatrixDto getMatrix(){
        return roleClaimService.getMatrix();
    }
    @PutMapping()
    @RequiresPermission("update")
    public ResponseEntity<?> updateRole(HttpServletRequest request, @RequestBody List<RoleClaimRequest> roleClaimRequests){
        Long userId = jwtService.extractUserIdFromAccessToken(request);
        roleClaimService.processRoleClaim(roleClaimRequests, userId);
        return ResponseEntity.ok("Matrix successfully updated");
    }


}
