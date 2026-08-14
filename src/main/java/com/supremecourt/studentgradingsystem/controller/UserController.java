package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.response.UserGetDto;
import com.supremecourt.studentgradingsystem.service.UserService;
import com.supremecourt.studentgradingsystem.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping
    @RequiresPermission("create_user")
    public void createUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        userService.createUser(userRegistrationDto);
    }

    @PatchMapping(value = "/change-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserGetDto changeImage(HttpServletRequest request,
                                  @RequestParam(value = "image", required = false) MultipartFile image) {
        Long userId = jwtService.extractUserIdFromAccessToken(request);
        return userService.changeImage(userId, image);
    }

    @DeleteMapping("/delete-image")
    public UserGetDto deleteImage(HttpServletRequest request) {
        Long userId = jwtService.extractUserIdFromAccessToken(request);
        return userService.deleteImage(userId);
    }
}
