package com.supremecourt.studentgradingsystem.controller.permission;

import com.supremecourt.studentgradingsystem.model.request.MenuSaveDto;
import com.supremecourt.studentgradingsystem.model.response.MenuResponseDto;
import com.supremecourt.studentgradingsystem.service.MenuService;
import com.supremecourt.studentgradingsystem.service.auth.JwtService;
import com.supremecourt.studentgradingsystem.service.permission.ClaimService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    private final JwtService jwtService;
    private final ClaimService claimService;

    @GetMapping()
    public List<MenuResponseDto> getMenusForCurrentUser(HttpServletRequest request) {
        String role = jwtService.extractRolesFromToken(request);
        List<Long> claimIds = claimService.getClaimIdsForRole(role);
        return menuService.getMenusForClaims(claimIds);
    }

    @PostMapping
    public void createMenu(@RequestBody MenuSaveDto menuSaveDto) {
        menuService.createMenu(menuSaveDto);
    }
}