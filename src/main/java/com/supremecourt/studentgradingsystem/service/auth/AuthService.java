package com.supremecourt.studentgradingsystem.service.auth;

import com.supremecourt.studentgradingsystem.dao.entity.RoleEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.RoleRepository;
import com.supremecourt.studentgradingsystem.dao.repository.UserRepository;
import com.supremecourt.studentgradingsystem.enums.EntranceType;
import com.supremecourt.studentgradingsystem.enums.ExceptionEnum;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.exception.OTPException;
import com.supremecourt.studentgradingsystem.exception.PasswordResetException;
import com.supremecourt.studentgradingsystem.exception.UserNotAuthorizedException;
import com.supremecourt.studentgradingsystem.mapper.UserMapper;
import com.supremecourt.studentgradingsystem.model.request.OTPRequestDto;
import com.supremecourt.studentgradingsystem.model.request.ResendOTPRequestDto;
import com.supremecourt.studentgradingsystem.model.request.ResetPasswordRequestDto;
import com.supremecourt.studentgradingsystem.model.request.UserRegistrationDto;
import com.supremecourt.studentgradingsystem.model.request.auth.AuthRequestDto;
import com.supremecourt.studentgradingsystem.model.request.auth.AuthenticationDto;
import com.supremecourt.studentgradingsystem.model.response.ResponseDto;
import com.supremecourt.studentgradingsystem.service.mail.EmailService;
import com.supremecourt.studentgradingsystem.utils.OTPCodeGenerator;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final JwtBlacklistService jwtBlacklistService;
//    private final AppleOAuth2Service appleOAuth2Service;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long OTP_TTL_SECONDS = 180L;
    private static final long SIGNUP_DATA_TTL_SECONDS = 600L;
    private static final int MAX_WRONG_OTP_ATTEMPTS = 5;
    private static final long WRONG_OTP_BLOCK_MINUTES = 5L;
    private static final long RESEND_LIMIT_SECONDS = 60L;
    private static final long RESET_PASSWORD_OTP_TTL_SECONDS = 180L;

    @Value("${GOOGLE_ANDROID_CLIENT_ID:}")
    private String androidClientId;

    @Value("${GOOGLE_IOS_CLIENT_ID:}")
    private String iosClientId;

    public String signUp(UserRegistrationDto dto) {
        log.info("ActionLog.signUp.start");
        String email = dto.getEmail();
        if (emailExists(email)) throw new NotFoundException(ExceptionEnum.USER_ALREADY_EXISTS.name(),
                String.format("User with email %s already exists", email));

        String otp = OTPCodeGenerator.generateCode();
        validatePasswordStrength(dto.getPassword());
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        redisTemplate.opsForValue().set(email + ":OTP", otp, OTP_TTL_SECONDS, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(email + ":Data", dto, SIGNUP_DATA_TTL_SECONDS, TimeUnit.SECONDS);

        try {
            emailService.sendOtp(email, otp);
        } catch (Exception e) {
            log.error("ActionLog.signUp.emailSend.failed email={}, error={}", email, e.getMessage());
            throw new OTPException("Failed to send OTP email", "Email sending error");
        }
        log.info("ActionLog.signUp.end");
        return "OTP sent to email";
    }

    public void validateOTP(OTPRequestDto otpRequestDto) {
        String email = otpRequestDto.getEmail();

        // Block check
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
                // set block
                redisTemplate.opsForValue().set(blockKey, 1, WRONG_OTP_BLOCK_MINUTES, TimeUnit.MINUTES);
            }
            throw new OTPException("OTP code is incorrect!", "Wrong OTP attempt " + next);
        }
        redisTemplate.delete(email + ":OTP:ATTEMPTS");
    }

    @Transactional
    public AuthenticationDto verifyOTP(OTPRequestDto otpRequestDto) {
        log.info("ActionLog.verifyOTP.start");

        validateOTP(otpRequestDto);

        String email = otpRequestDto.getEmail();
        String keyData = email + ":Data";

        if (EntranceType.SIGNUP.equals(otpRequestDto.getEntranceType())) {
            Object cachedObj = redisTemplate.opsForValue().get(keyData);
            if (cachedObj == null) throw new NotFoundException("User data not found in cache!", "Cache error");

            UserRegistrationDto dto = objectMapper.convertValue(cachedObj, UserRegistrationDto.class);

            AuthenticationDto authDto = completeSignUp(dto);

            redisTemplate.delete(email + ":OTP");
            redisTemplate.delete(keyData);

            log.info("ActionLog.verifyOTP.end");
            return authDto;
        }
        else {
            UserEntity user = getUserByEmailOrThrow(email);
            redisTemplate.delete(email + ":OTP");
            return generateToken(user);
        }
    }

    public AuthenticationDto authenticate(AuthRequestDto authRequestDto) {
        log.info("ActionLog.authenticate.start");
        try {
            UserEntity user = userRepository.findByEmail(authRequestDto.getEmail()).orElse(null);

            if (user == null) {
                passwordEncoder.matches(authRequestDto.getPassword(),
                        "$2a$10$dummyHashForTimingAttackPrevention");
                throw new UserNotAuthorizedException(
                        ExceptionEnum.EMAIL_OR_PASSWORD_INCORRECT.name(),
                        ExceptionEnum.EMAIL_OR_PASSWORD_INCORRECT.getLog()
                );
            }

            verifyPassword(authRequestDto.getPassword(), user.getPassword());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.info("ActionLog.authenticate.end");
            var accessToken = jwtService.generateToken(user);
            return AuthenticationDto.builder()
                    .token(accessToken)
                    .build();

        } catch (UserNotAuthorizedException e) {
            log.warn("ActionLog.authenticate.failed");  // Detaylı mesaj verme
            throw new UserNotAuthorizedException(
                    ExceptionEnum.EMAIL_OR_PASSWORD_INCORRECT.name(),
                    ExceptionEnum.EMAIL_OR_PASSWORD_INCORRECT.getLog()
            );
        }
    }

    private void verifyPassword(String rawPassword, String encodedPassword) {
        log.info("ActionLog.verifyPassword.start");
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            log.warn("ActionLog.verifyPassword.failed: Passwords don't match");
            throw new UserNotAuthorizedException(
                    ExceptionEnum.EMAIL_OR_PASSWORD_INCORRECT.name(),
                    ExceptionEnum.EMAIL_OR_PASSWORD_INCORRECT.getLog()
            );
        }
        log.info("ActionLog.verifyPassword.end");
    }

    @Transactional
    public AuthenticationDto completeSignUp(UserRegistrationDto dto) {
        UserEntity user = userMapper.mapUserRegistrationDtoToEntity(dto);
        setRolesAndSave(user);
        return generateToken(user);
    }

    public String resendOTP(ResendOTPRequestDto dto) {
        log.info("Resend OTP Started...");
        String email = dto.getEmail();

        // rate limit
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

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Transactional
    public AuthenticationDto loginWithGoogle(String idToken) {
        log.info("ActionLog.loginWithGoogle.start");

        Integer tokenLen = (idToken == null) ? null : idToken.length();
        String tokenPrefix = (idToken == null) ? null : idToken.substring(0, Math.min(12, idToken.length()));
        log.info("ActionLog.loginWithGoogle.input tokenLen={}, tokenPrefix={}, clientIdPresent={}",
                tokenLen, tokenPrefix, StringUtils.hasText(googleClientId));

        if (!StringUtils.hasText(googleClientId)) {
            log.warn("ActionLog.loginWithGoogle.failed reason=googleClientId_empty");
            throw new IllegalStateException("googleClientId is empty. Check GOOGLE_CLIENT_ID env / application.yml mapping");
        }
        if (!StringUtils.hasText(idToken)) {
            log.warn("ActionLog.loginWithGoogle.failed reason=idToken_empty");
            throw new IllegalArgumentException("idToken is empty");
        }
        if (idToken.chars().filter(ch -> ch == '.').count() != 2) {
            log.warn("ActionLog.loginWithGoogle.failed reason=idToken_not_jwt tokenPrefix={}", tokenPrefix);
            throw new IllegalArgumentException("idToken is not a JWT (must contain 2 dots)");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                log.warn("ActionLog.loginWithGoogle.failed reason=google_verify_null tokenPrefix={}", tokenPrefix);
                throw new UserNotAuthorizedException(
                        ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                        "Invalid Google ID token"
                );
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            Boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String sub = payload.getSubject();

            log.info("ActionLog.loginWithGoogle.googleVerified email={}, emailVerified={}, sub={}",
                    email, emailVerified, sub);

            if (!StringUtils.hasText(email) || !Boolean.TRUE.equals(emailVerified)) {
                log.warn("ActionLog.loginWithGoogle.failed reason=email_not_verified email={}, emailVerified={}",
                        email, emailVerified);
                throw new UserNotAuthorizedException(
                        ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                        "Google account email is not verified"
                );
            }

            UserEntity userEntity = userRepository.findByEmail(email).orElse(null);

            if (userEntity == null) {
                log.info("ActionLog.loginWithGoogle.userCreate.start email={}", email);

                userEntity = new UserEntity();
                userEntity.setEmail(email);
                userEntity.setFullName(name);

                RoleEntity role = roleRepository.findByName("USER")
                        .orElseThrow(() -> new NotFoundException(
                                ExceptionEnum.ROLE_NOT_FOUND.name(),
                                String.format(ExceptionEnum.ROLE_NOT_FOUND.getLog(), "USER")
                        ));
                userEntity.setRole(role);

                userRepository.save(userEntity);

                log.info("ActionLog.loginWithGoogle.userCreate.end userId={}, email={}",
                        userEntity.getId(), email);
            } else {
                log.info("ActionLog.loginWithGoogle.userFound userId={}, email={}",
                        userEntity.getId(), email);
            }

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            userEntity, null, userEntity.getAuthorities()
                    )
            );

            AuthenticationDto auth = generateToken(userEntity);

            log.info("ActionLog.loginWithGoogle.end userId={}, email={}", userEntity.getId(), email);
            return auth;

        } catch (UserNotAuthorizedException e) {
            // burada message-i saxla, stacktrace lazım deyil (adətən)
            log.warn("ActionLog.loginWithGoogle.failed message={}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("ActionLog.loginWithGoogle.failed error={}", e.getMessage(), e);
            throw new UserNotAuthorizedException(
                    ExceptionEnum.USER_NOT_AUTHORIZED.name(),
                    "Error verifying Google ID token"
            );
        }
    }

    @Transactional
//    public AuthenticationDto loginWithGoogle(String idToken) {
//        log.info("ActionLog.loginWithGoogle.start");
//
//        Integer tokenLen = (idToken == null) ? null : idToken.length();
//        String tokenPrefix = (idToken == null) ? null : idToken.substring(0, Math.min(12, idToken.length()));
//        log.info("ActionLog.loginWithGoogle.input tokenLen={}, tokenPrefix={}, androidIdPresent={}, iosIdPresent={}",
//                tokenLen, tokenPrefix,
//                StringUtils.hasText(androidClientId),
//                StringUtils.hasText(iosClientId));
//        if (!StringUtils.hasText(idToken)) {
//            log.warn("ActionLog.loginWithGoogle.failed reason=idToken_empty");
//            throw new IllegalArgumentException("idToken is empty");
//        }
//        if (idToken.chars().filter(ch -> ch == '.').count() != 2) {
//            log.warn("ActionLog.loginWithGoogle.failed reason=idToken_not_jwt tokenPrefix={}", tokenPrefix);
//            throw new IllegalArgumentException("idToken is not a JWT (must contain 2 dots)");
//        }
//
//        // Audience list — yalnız dolu olanları əlavə et
//        List<String> audience = new ArrayList<>();
//        if (StringUtils.hasText(androidClientId)) audience.add(androidClientId);
//        if (StringUtils.hasText(iosClientId)) audience.add(iosClientId);
//
//        if (audience.isEmpty()) {
//            log.warn("ActionLog.loginWithGoogle.failed reason=client_ids_empty");
//            throw new IllegalStateException("No Google client IDs configured. Set GOOGLE_ANDROID_CLIENT_ID / GOOGLE_IOS_CLIENT_ID (and optionally GOOGLE_WEB_CLIENT_ID).");
//        }
//
//        try {
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//                    new NetHttpTransport(), GsonFactory.getDefaultInstance()
//            )
//                    .setAudience(audience)
//                    .setIssuer("https://accounts.google.com")
//                    .build();
//
//            GoogleIdToken googleIdToken = verifier.verify(idToken);
//
//            if (googleIdToken == null) {
//                log.warn("ActionLog.loginWithGoogle.failed reason=google_verify_null tokenPrefix={}", tokenPrefix);
//                throw new UserNotAuthorizedException(
//                        ExceptionEnum.USER_NOT_AUTHORIZED.name(),
//                        "Invalid Google ID token"
//                );
//            }
//
//            GoogleIdToken.Payload payload = googleIdToken.getPayload();
//            String email = payload.getEmail();
//            Boolean emailVerified = payload.getEmailVerified();
//            String name = (String) payload.get("name");
//            String sub = payload.getSubject();
//            String aud = (String) payload.getAudience();
//
//            log.info("ActionLog.loginWithGoogle.googleVerified email={}, emailVerified={}, sub={}, aud={}",
//                    email, emailVerified, sub, aud);
//
//            if (!StringUtils.hasText(email) || !Boolean.TRUE.equals(emailVerified)) {
//                log.warn("ActionLog.loginWithGoogle.failed reason=email_not_verified email={}, emailVerified={}",
//                        email, emailVerified);
//                throw new UserNotAuthorizedException(
//                        ExceptionEnum.USER_NOT_AUTHORIZED.name(),
//                        "Google account email is not verified"
//                );
//            }
//
//            UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
//
//            if (userEntity == null) {
//                log.info("ActionLog.loginWithGoogle.userCreate.start email={}", email);
//
//                userEntity = new UserEntity();
//                userEntity.setEmail(email);
//                userEntity.setFullName(name);
//                // TODO: role provider set, etc.
//
//                userRepository.save(userEntity);
//
//                log.info("ActionLog.loginWithGoogle.userCreate.end userId={}, email={}",
//                        userEntity.getId(), email);
//            } else {
//                log.info("ActionLog.loginWithGoogle.userFound userId={}, email={}",
//                        userEntity.getId(), email);
//            }
//
//            SecurityContextHolder.getContext().setAuthentication(
//                    new UsernamePasswordAuthenticationToken(
//                            userEntity, null, userEntity.getAuthorities()
//                    )
//            );
//
//            AuthenticationDto auth = AuthenticationDto.builder()
//                    .token( jwtService.generateToken(userEntity)).build();
//
//            log.info("ActionLog.loginWithGoogle.end userId={}, email={}", userEntity.getId(), email);
//            return auth;
//
//        } catch (UserNotAuthorizedException e) {
//            log.warn("ActionLog.loginWithGoogle.failed message={}", e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            log.error("ActionLog.loginWithGoogle.failed error={}", e.getMessage(), e);
//            throw new UserNotAuthorizedException(
//                    ExceptionEnum.USER_NOT_AUTHORIZED.name(),
//                    "Error verifying Google ID token"
//            );
//        }
//    }
    public AuthenticationDto refreshToken(String oldAccessToken) {
        log.info("Refresh Token Started...");

        UserDetails userDetails = getAccount();
        jwtBlacklistService.addBlacklist(oldAccessToken, 5L);
        return generateToken(userDetails);
    }

    public void logout(String accessToken) {
        log.info("Log Out Started...");

        jwtBlacklistService.addBlacklist(accessToken, 19L);

        log.info("Log Out Ended");
    }


    public static UserDetails getAccount() {
        log.info("Get Account Started...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return (UserEntity) authentication.getPrincipal();
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).orElse(null) != null;
    }

    private boolean checkOTP(String cachedOTP, String OTP) {
        return cachedOTP != null && cachedOTP.equals(OTP);
    }

    private boolean expiryOTP(Long TTL) {
        return TTL == null || TTL < 0;
    }

    private void setRolesAndSave(UserEntity userEntity) {
        RoleEntity role = roleRepository.findByName("USER")
                .orElseThrow(() -> new NotFoundException(
                        ExceptionEnum.ROLE_NOT_FOUND.name(),
                        String.format(ExceptionEnum.ROLE_NOT_FOUND.getLog(), "USER")
                ));
        userEntity.setRole(role);
        userRepository.save(userEntity);
    }

    private AuthenticationDto generateToken(UserDetails user) {
        String token = jwtService.generateToken(user);
        return AuthenticationDto.builder()
                .token(token)
                .build();
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
    public AuthenticationDto resetPassword(ResetPasswordRequestDto requestDto) {

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

        return generateToken(user);
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
