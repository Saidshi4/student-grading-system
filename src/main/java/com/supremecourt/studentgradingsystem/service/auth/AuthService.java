package com.supremecourt.studentgradingsystem.service.auth;

import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.UserRepository;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.JwtExpiredException;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.exception.OTPException;
import com.supremecourt.studentgradingsystem.exception.PasswordResetException;
import com.supremecourt.studentgradingsystem.exception.UserNotAuthorizedException;
import com.supremecourt.studentgradingsystem.model.request.OTPRequestDto;
import com.supremecourt.studentgradingsystem.model.request.ResendOTPRequestDto;
import com.supremecourt.studentgradingsystem.model.request.ResetPasswordRequestDto;
import com.supremecourt.studentgradingsystem.model.request.auth.AuthRequestDto;
import com.supremecourt.studentgradingsystem.model.request.auth.AuthenticationDto;
import com.supremecourt.studentgradingsystem.model.response.ResponseDto;
import com.supremecourt.studentgradingsystem.service.mail.EmailService;
import com.supremecourt.studentgradingsystem.utils.OTPCodeGenerator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtBlacklistService jwtBlacklistService;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String REFRESH_COOKIE_PATH = "/auth";
    private static final long OTP_TTL_SECONDS = 180L;
    private static final int MAX_WRONG_OTP_ATTEMPTS = 5;
    private static final long WRONG_OTP_BLOCK_MINUTES = 5L;
    private static final long RESEND_LIMIT_SECONDS = 60L;
    private static final long RESET_PASSWORD_OTP_TTL_SECONDS = 180L;

    public void validateOTP(OTPRequestDto otpRequestDto) {
        String email = otpRequestDto.getEmail();

        String blockKey = email + ":OTP:BLOCK";
        Long blockTtl = redisTemplate.getExpire(blockKey, TimeUnit.SECONDS);
        if (blockTtl > 0) {
            throw new OTPException("Too many wrong attempts. Try again later.", "OTP blocked for " + blockTtl + " seconds");
        }

        String keyOTP = email + ":OTP";
        String cachedOTP = (String) redisTemplate.opsForValue().get(keyOTP);

        Long TTL = redisTemplate.getExpire(keyOTP, TimeUnit.SECONDS);
        if (expiryOTP(TTL))
            throw new OTPException("OTP code has expired!", "OTP expired");

        if (!checkOTP(cachedOTP, otpRequestDto.getOtp())) {
            String attemptsKey = email + ":OTP:ATTEMPTS";
            Integer attempts = (Integer) redisTemplate.opsForValue().get(attemptsKey);
            int next = attempts == null ? 1 : attempts + 1;
            redisTemplate.opsForValue().set(attemptsKey, next, WRONG_OTP_BLOCK_MINUTES, TimeUnit.MINUTES);
            if (next >= MAX_WRONG_OTP_ATTEMPTS) {
                redisTemplate.opsForValue().set(blockKey, 1, WRONG_OTP_BLOCK_MINUTES, TimeUnit.MINUTES);
            }
            throw new OTPException("OTP code is incorrect!", "Wrong OTP attempt " + next);
        }
        redisTemplate.delete(email + ":OTP:ATTEMPTS");
    }

    @Transactional
    public AuthenticationDto verifyOTP(OTPRequestDto otpRequestDto, HttpServletResponse response) {
        log.info("ActionLog.verifyOTP.start");

        validateOTP(otpRequestDto);

        UserEntity user = getUserByEmailOrThrow(otpRequestDto.getEmail());
        redisTemplate.delete(otpRequestDto.getEmail() + ":OTP");

        log.info("ActionLog.verifyOTP.end");
        return issueTokens(user, response);
    }

    public AuthenticationDto authenticate(AuthRequestDto authRequestDto, HttpServletResponse response) {
        log.info("ActionLog.authenticate.start");
        try {
            UserEntity user = userRepository.findByUsername(authRequestDto.getUsername()).orElse(null);

            if (user == null) {
                passwordEncoder.matches(authRequestDto.getPassword(),
                        "$2a$10$dummyHashForTimingAttackPrevention");
                throw new UserNotAuthorizedException(
                        ExceptionEnum.USERNAME_OR_PASSWORD_INCORRECT.name(),
                        ExceptionEnum.USERNAME_OR_PASSWORD_INCORRECT.getLog()
                );
            }

            verifyPassword(authRequestDto.getPassword(), user.getPassword());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.info("ActionLog.authenticate.end");
            return issueTokens(user, response);

        } catch (UserNotAuthorizedException e) {
            log.warn("ActionLog.authenticate.failed");
            throw new UserNotAuthorizedException(
                    ExceptionEnum.USERNAME_OR_PASSWORD_INCORRECT.name(),
                    ExceptionEnum.USERNAME_OR_PASSWORD_INCORRECT.getLog()
            );
        }
    }

    private void verifyPassword(String rawPassword, String encodedPassword) {
        log.info("ActionLog.verifyPassword.start");
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            log.warn("ActionLog.verifyPassword.failed: Passwords don't match");
            throw new UserNotAuthorizedException(
                    ExceptionEnum.USERNAME_OR_PASSWORD_INCORRECT.name(),
                    ExceptionEnum.USERNAME_OR_PASSWORD_INCORRECT.getLog()
            );
        }
        log.info("ActionLog.verifyPassword.end");
    }

    public String resendOTP(ResendOTPRequestDto dto) {
        log.info("Resend OTP Started...");
        String email = dto.getEmail();

        String resendKey = email + ":OTP:RESEND_LIMIT";
        String marker = (String) redisTemplate.opsForValue().get(resendKey);
        if (StringUtils.hasText(marker)) {
            throw new OTPException("OTP resend is limited. Try again later.", "Resend OTP rate limit active");
        }
        redisTemplate.opsForValue().set(resendKey, "1", RESEND_LIMIT_SECONDS, TimeUnit.SECONDS);

        String otp = OTPCodeGenerator.generateCode();
        redisTemplate.opsForValue().set(email + ":OTP", otp, OTP_TTL_SECONDS, TimeUnit.SECONDS);

        try {
            emailService.sendOtp(email, otp);
        } catch (Exception e) {
            log.error("ActionLog.resendOTP.emailSend.failed email={}, error={}", email, e.getMessage());
            throw new OTPException("Failed to send OTP email", "Email sending error");
        }
        log.info("Resend OTP Ended");
        return "OTP resent to email";
    }

    @Transactional
    public AuthenticationDto refreshToken(String refreshToken, HttpServletResponse response) {
        log.info("ActionLog.refreshToken.start");

        if (!StringUtils.hasText(refreshToken)) {
            throw new UserNotAuthorizedException(
                    ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                    "Refresh token is missing"
            );
        }

        if (jwtBlacklistService.isBlacklisted(refreshToken)) {
            throw new UserNotAuthorizedException(
                    ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                    "Refresh token is blacklisted"
            );
        }

        try {
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new UserNotAuthorizedException(
                        ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                        "Invalid refresh token"
                );
            }

            String username = jwtService.extractUsername(refreshToken);
            UserEntity user = userRepository.findByUsername(username).orElseThrow(() ->
                    new UserNotAuthorizedException(
                            ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                            "User not found for refresh token"
                    ));

            if (!user.isEnabled()) {
                throw new UserNotAuthorizedException(
                        ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                        "Account is deactivated"
                );
            }

            if (!jwtService.isTokenVersionValid(refreshToken, user)) {
                throw new UserNotAuthorizedException(
                        ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                        "Refresh token version mismatch"
                );
            }

            jwtBlacklistService.addBlacklist(refreshToken, jwtService.getRemainingTtlMinutes(refreshToken));
            log.info("ActionLog.refreshToken.end");
            return issueTokens(user, response);
        } catch (ExpiredJwtException e) {
            clearRefreshCookie(response);
            throw new JwtExpiredException(
                    ExceptionEnum.JWT_TOKEN_EXPIRED.name(),
                    String.format(ExceptionEnum.JWT_TOKEN_EXPIRED.getLog(), "refresh")
            );
        } catch (JwtException e) {
            throw new UserNotAuthorizedException(
                    ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                    "Invalid refresh token"
            );
        }
    }

    public void logout(String accessToken, String refreshToken, HttpServletResponse response) {
        log.info("ActionLog.logout.start");
        blacklistIfPresent(accessToken);
        blacklistIfPresent(refreshToken);
        clearRefreshCookie(response);
        log.info("ActionLog.logout.end");
    }

    private boolean checkOTP(String cachedOTP, String OTP) {
        return cachedOTP != null && cachedOTP.equals(OTP);
    }

    private boolean expiryOTP(Long TTL) {
        return TTL == null || TTL < 0;
    }

    private AuthenticationDto issueTokens(UserEntity user, HttpServletResponse response) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        setRefreshCookie(response, refreshToken);
        return AuthenticationDto.builder()
                .token(accessToken)
                .build();
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(Duration.ofMillis(jwtService.getRefreshExpirationMs()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void blacklistIfPresent(String token) {
        if (!StringUtils.hasText(token)) {
            return;
        }
        try {
            jwtBlacklistService.addBlacklist(token, jwtService.getRemainingTtlMinutes(token));
        } catch (Exception e) {
            log.warn("ActionLog.blacklistIfPresent.skipped error={}", e.getMessage());
        }
    }

    private UserEntity getUserByEmailOrThrow(String email) {
        log.info("ActionLog.getUserByEmailOrThrow.start email={}", email);
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new NotFoundException(
                    ExceptionEnum.USER_NOT_FOUND_BY_EMAIL.name(),
                    String.format(ExceptionEnum.USER_NOT_FOUND_BY_EMAIL.getLog(), email)
            );
        }
        log.info("ActionLog.getUserByEmailOrThrow.end email={}", email);
        return user;
    }

    public ResponseDto forgotPassword(String email) {
        log.info("ActionLog.forgotPassword.start");

        long startTime = System.currentTimeMillis();
        long minDuration = 500;

        UserEntity user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            try {
                String token = OTPCodeGenerator.generateResetPasswordToken();
                redisTemplate.opsForValue().set(
                        token, email, RESET_PASSWORD_OTP_TTL_SECONDS, TimeUnit.SECONDS
                );
                CompletableFuture.runAsync(() -> {
                    try {
                        emailService.sendResetPasswordEmail(email, token);
                    } catch (Exception e) {
                        log.error("ActionLog.forgotPassword.emailSend.failed error={}", e.getMessage());
                    }
                });
            } catch (Exception e) {
                log.error("ActionLog.forgotPassword.failed error={}", e.getMessage());
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed < minDuration) {
            try {
                Thread.sleep(minDuration - elapsed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("ActionLog.forgotPassword.end");
        return ResponseDto.builder()
                .message("LINK_SENT_IF_EMAIL_EXISTS")
                .build();
    }

    @Transactional
    public AuthenticationDto resetPassword(ResetPasswordRequestDto requestDto, HttpServletResponse response) {

        String token = requestDto.getToken();
        String newPassword = requestDto.getNewPassword();

        String tokenPrefix = token.substring(0, Math.min(12, token.length()));
        log.info("ActionLog.resetPassword.start tokenPrefix={}", tokenPrefix);

        String email = (String) redisTemplate.opsForValue().get(token);
        if (!StringUtils.hasText(email)) {
            log.warn("ActionLog.resetPassword.failed reason=token_invalid_or_expired tokenPrefix={}",
                    tokenPrefix);
            throw new PasswordResetException(
                    "RESET_PASSWORD_TOKEN_INVALID_OR_EXPIRED",
                    "Invalid reset password token"
            );
        }
        UserEntity user = getUserByEmailOrThrow(email);
        validatePasswordStrength(newPassword);
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PasswordResetException(
                    "NEW_PASSWORD_SAME_AS_OLD",
                    "Same password"
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(Instant.now());
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
        redisTemplate.delete(token);
        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendPasswordChangedNotification(email);
            } catch (Exception e) {
                log.error("ActionLog.resetPassword.notification.failed error={}", e.getMessage());
            }
        });
        log.info("ActionLog.resetPassword.success");

        return issueTokens(user, response);
    }

    private void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new PasswordResetException("Password must be at least 8 characters", "Weak password");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new PasswordResetException("Password must contain at least one uppercase letter", "Weak password");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new PasswordResetException("Password must contain at least one lowercase letter", "Weak password");
        }
        if (!password.matches(".*\\d.*")) {
            throw new PasswordResetException("Password must contain at least one number", "Weak password");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new PasswordResetException("Password must contain at least one special character", "Weak password");
        }
    }

}
