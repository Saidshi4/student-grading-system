package com.supremecourt.studentgradingsystem.controller;

import com.supremecourt.studentgradingsystem.annotation.RequiresPermission;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.request.UserUpdateDto;
import com.supremecourt.studentgradingsystem.model.response.UserGetDto;
import com.supremecourt.studentgradingsystem.service.UserService;
import com.supremecourt.studentgradingsystem.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission("create_user")
    public UserGetDto createUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        return userService.getById(userService.createUser(userRegistrationDto).getId());
    }

    @GetMapping
    @RequiresPermission("user_read")
    public List<UserGetDto> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @RequiresPermission("user_read")
    public UserGetDto getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    @RequiresPermission("user_update")
    public UserGetDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto userUpdateDto) {
        return userService.update(id, userUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission("user_delete")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
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
