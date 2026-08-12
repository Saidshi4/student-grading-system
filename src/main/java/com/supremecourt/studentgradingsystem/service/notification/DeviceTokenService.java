package com.supremecourt.studentgradingsystem.service.notification;

import com.supremecourt.studentgradingsystem.dao.entity.DeviceTokenEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.DeviceTokenRepository;
import com.supremecourt.studentgradingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserService userService;

    @Transactional
    public void saveToken(Long userId, String token) {
        log.info("ActionLog.saveDeviceToken.start userId {}", userId);
        UserEntity user = userService.findById(userId);

        deviceTokenRepository.findByTokenAndUserEntityId(token, userId)
                .ifPresentOrElse(
                        existing -> log.info("ActionLog.saveDeviceToken.alreadyExists userId {}", userId),
                        () -> {
                            DeviceTokenEntity entity = DeviceTokenEntity.builder()
                                    .token(token)
                                    .userEntity(user)
                                    .build();
                            deviceTokenRepository.save(entity);
                            log.info("ActionLog.saveDeviceToken.saved userId {}", userId);
                        }
                );
        log.info("ActionLog.saveDeviceToken.end userId {}", userId);
    }

    @Transactional
    public void deleteToken(Long userId, String token) {
        log.info("ActionLog.deleteDeviceToken.start userId {}", userId);
        int deleted = deviceTokenRepository.deleteByTokenAndUserId(token, userId);
        log.info("ActionLog.deleteDeviceToken.end userId {} deleted {}", userId, deleted);
    }
}
