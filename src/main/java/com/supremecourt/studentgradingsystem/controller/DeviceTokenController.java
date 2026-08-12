package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.model.request.DeviceTokenRequestDto;
import com.supremecourt.studentgradingsystem.service.auth.JwtService;
import com.supremecourt.studentgradingsystem.service.notification.DeviceTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/device-token")
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<Void> registerToken(HttpServletRequest request, @RequestBody DeviceTokenRequestDto dto) {
        Long userId = jwtService.extractUserIdFromAccessToken(request);
        deviceTokenService.saveToken(userId, dto.getToken());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeToken(HttpServletRequest request, @RequestBody DeviceTokenRequestDto dto) {
        Long userId = jwtService.extractUserIdFromAccessToken(request);
        deviceTokenService.deleteToken(userId, dto.getToken());
        return ResponseEntity.ok().build();
    }
}
