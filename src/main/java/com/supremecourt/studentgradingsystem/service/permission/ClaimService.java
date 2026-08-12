package com.supremecourt.studentgradingsystem.service.permission;


import com.supremecourt.studentgradingsystem.dao.entity.ClaimsEntity;
import com.supremecourt.studentgradingsystem.dao.repository.ClaimRepository;
import com.supremecourt.studentgradingsystem.mapper.ClaimMapper;
import com.supremecourt.studentgradingsystem.model.request.ClaimSaveDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;

    public List<Long> getClaimIdsForRole(String roleName) {
        log.info("ActionLog.getClaimIdsForRole.start by roleName {}",roleName);
        List<Long> claimIds = claimRepository.findClaimIdsByRoleName(roleName);
        log.info("ActionLog.getClaimIdsForRole.end by roleName {}",roleName);
        return claimIds;
    }

    public Long createClaim(ClaimSaveDto claimSaveDto){
        log.info("ActionLog.createClaim.start");
        ClaimsEntity claimsEntity = claimMapper.mapSaveDtoToEntity(claimSaveDto);
        ClaimsEntity savedClaim= claimRepository.save(claimsEntity);
        log.info("ActionLog.createClaim.end");
        return savedClaim.getId();
    }
}
