package com.supremecourt.studentgradingsystem.service.permission;

import com.supremecourt.studentgradingsystem.dao.entity.ClaimEntity;
import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.RolesClaimsEntity;
import com.supremecourt.studentgradingsystem.dao.repository.ClaimRepository;
import com.supremecourt.studentgradingsystem.dao.repository.RoleRepository;
import com.supremecourt.studentgradingsystem.dao.repository.RolesClaimsRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.ClaimMapper;
import com.supremecourt.studentgradingsystem.mapper.RoleClaimMapper;
import com.supremecourt.studentgradingsystem.mapper.RoleMapper;
import com.supremecourt.studentgradingsystem.model.matrix.MatrixDto;
import com.supremecourt.studentgradingsystem.model.matrix.RoleClaimRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleClaimService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final RolesClaimsRepository rolesClaimsRepository;
    private final RoleClaimMapper roleClaimMapper;
    public MatrixDto getMatrix(){
        log.info("ActionLog.getMatrix.start");
        MatrixDto matrixDto=new MatrixDto();
        matrixDto.setRoles(roleMapper.mapEntityToGetDtos(roleRepository.findAll()));
        matrixDto.setClaims(claimMapper.mapEntityToGetDtos(claimRepository.findAll()));
        matrixDto.setRolesClaims(roleClaimMapper.mapEntityToGetDtos(rolesClaimsRepository.findAll()));
        log.info("ActionLog.getMatrix.end");
        return matrixDto;
    }
    @Transactional
    public void processRoleClaim(List<RoleClaimRequest> roleClaimRequest, Long userId) {
        log.info("ActionLog.processRoleClaim.start userId = {}", userId);
        for(RoleClaimRequest roleClaimRequest1:roleClaimRequest) {
            Optional<RolesClaimsEntity> roleClaim = rolesClaimsRepository.findByRoleIdAndClaimId(roleClaimRequest1.getRoleId(), roleClaimRequest1.getClaimId());
            if (roleClaimRequest1.getHasPermission()) {
                if (roleClaim.isEmpty()) {
                    RoleEntity role=roleRepository.findById(roleClaimRequest1.getRoleId()).orElseThrow(()->{
                        throw new NotFoundException("Rol tapılmadı!",
                                String.format(ExceptionEnum.ROLE_NOT_FOUND.getLog(), roleClaimRequest1.getRoleId()));
                    });
                    ClaimEntity claim=claimRepository.findById(roleClaimRequest1.getClaimId()).orElseThrow(()->{
                        throw new NotFoundException("Claim tapılmadı!",
                                String.format(ExceptionEnum.CLAIM_NOT_FOUND.getLog(), roleClaimRequest1.getClaimId()));
                    });
                    RolesClaimsEntity newRoleClaim = RolesClaimsEntity.builder()
                            .role(role)
                            .claim(claim)
                            .build();
                    rolesClaimsRepository.save(newRoleClaim);
                }
            } else {
                roleClaim.ifPresent(rc -> rolesClaimsRepository.deleteByRoleIdAndClaimId(roleClaimRequest1.getRoleId(), roleClaimRequest1.getClaimId()));
            }
        }
        log.info("ActionLog.processRoleClaim.end userId = {}", userId);
    }

}
