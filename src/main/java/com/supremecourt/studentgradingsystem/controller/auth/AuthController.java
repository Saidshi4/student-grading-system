package com.supremecourt.studentgradingsystem.controller.auth;

import com.supremecourt.studentgradingsystem.model.request.OTPRequestDto;
import com.supremecourt.studentgradingsystem.model.request.ResendOTPRequestDto;
import com.supremecourt.studentgradingsystem.model.request.ResetPasswordRequestDto;
import com.supremecourt.studentgradingsystem.model.request.auth.AuthRequestDto;
import com.supremecourt.studentgradingsystem.model.request.auth.AuthenticationDto;
import com.supremecourt.studentgradingsystem.model.response.ResponseDto;
import com.supremecourt.studentgradingsystem.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthenticationDto login(@RequestBody AuthRequestDto authRequestDto,
                                   HttpServletResponse response) {
        return authService.authenticate(authRequestDto, response);
    }

    @PostMapping("/forgot-password")
    public ResponseDto forgotPassword(@RequestParam("email") String email) {
        return authService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public AuthenticationDto resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto,
                                           HttpServletResponse response) {
        return authService.resetPassword(resetPasswordRequestDto, response);
    }

    @PostMapping("/verify/otp")
    public AuthenticationDto verifyOTP(@RequestBody OTPRequestDto otpRequestDto,
                                       HttpServletResponse response) {
        return authService.verifyOTP(otpRequestDto, response);
    }

    @PostMapping("/refresh/token")
    public AuthenticationDto refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        return authService.refreshToken(refreshToken, response);
    }

    @PostMapping("/logout")
    public void logout(@RequestParam("accessToken") String accessToken,
                       @CookieValue(value = "refreshToken", required = false) String refreshToken,
                       HttpServletResponse response) {
        authService.logout(accessToken, refreshToken, response);
    }

    @PostMapping("/resend/otp")
    public String resendOTP(@RequestBody ResendOTPRequestDto resendOTPRequestDto) {
        return authService.resendOTP(resendOTPRequestDto);
    }
}
