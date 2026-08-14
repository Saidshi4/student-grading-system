package com.supremecourt.studentgradingsystem.controller.permission;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.ClaimSaveDto;
import com.supremecourt.studentgradingsystem.service.permission.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/claims")
@RequiredArgsConstructor
public class ClaimController {
    private final ClaimService claimService;

    @PostMapping
    @RequiresPermission("create")
    public Long createClaim(@RequestBody ClaimSaveDto claimSaveDto) {
        return claimService.createClaim(claimSaveDto);
    }
}
