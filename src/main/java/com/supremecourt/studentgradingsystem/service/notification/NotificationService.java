package com.supremecourt.studentgradingsystem.service.notification;

import com.supremecourt.studentgradingsystem.dao.entity.NotificationEntity;
import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.dao.repository.NotificationRepository;
import com.supremecourt.studentgradingsystem.enums.NotificationType;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import com.supremecourt.studentgradingsystem.mapper.NotificationMapper;
import com.supremecourt.studentgradingsystem.model.response.Note;
import com.supremecourt.studentgradingsystem.model.response.NotificationResponseDto;
import com.supremecourt.studentgradingsystem.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final ExpoPushService expoPushService;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserService userService;

    public Page<NotificationResponseDto> findAllByUserId(Long userId, int page, int size) {
        log.info("ActionLog.findAllByUserId.start userId {}", userId);

        userService.findById(userId);

        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationEntity> notificationPage = notificationRepository.findByUserEntityId(userId, pageable);

        List<NotificationResponseDto> notificationGetDtos = notificationMapper.mapToDtos(notificationPage.getContent());

        log.info("ActionLog.findAllByUserId.end userId {}", userId);
        return new PageImpl<>(notificationGetDtos, notificationPage.getPageable(), notificationPage.getTotalElements());
    }

    public NotificationResponseDto findById(Long notificationId, Long userId) {
        log.info("ActionLog.findNotificationById.start notificationId {} userId {}", notificationId, userId);
        NotificationEntity notification = notificationRepository.findByIdAndUserEntityId(notificationId, userId)
                .orElseThrow(() -> new NotFoundException("NOTIFICATION_NOT_FOUND", "Notification not found for user"));
        NotificationResponseDto dto = notificationMapper.mapToDto(notification);
        log.info("ActionLog.findNotificationById.end notificationId {} userId {}", notificationId, userId);
        return dto;
    }

    @Transactional
    public void saveAndSendNotification(UserEntity user, Note note, NotificationType type, Long relatedId) {
        log.info("ActionLog.saveAndSendNotification.start user {} type {}", user.getFullName(), type);

        NotificationEntity notification = NotificationEntity.builder()
                .title(note.getSubject())
                .content(note.getContent())
                .type(type)
                .relatedId(relatedId)
                .userEntity(user)
                .build();
        notificationRepository.save(notification);

        Hibernate.initialize(user.getDeviceTokenEntities());
        if (user.getDeviceTokenEntities() != null) {
            user.getDeviceTokenEntities().forEach(deviceTokenEntity -> {
                expoPushService.sendNotification(note, deviceTokenEntity.getToken());
            });
        }

        log.info("ActionLog.saveAndSendNotification.end user {} type {}", user.getFullName(), type);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.info("ActionLog.markAsRead.start notificationId {} userId {}", notificationId, userId);
        NotificationEntity notification = notificationRepository.findByIdAndUserEntityId(notificationId, userId)
                .orElseThrow(() -> new NotFoundException("NOTIFICATION_NOT_FOUND", "Notification not found for user"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
        log.info("ActionLog.markAsRead.end notificationId {} userId {}", notificationId, userId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("ActionLog.markAllAsRead.start userId {}", userId);
        int updated = notificationRepository.markAllAsReadByUserId(userId);
        log.info("ActionLog.markAllAsRead.end userId {} updated {}", userId, updated);
    }

    public long getUnreadCount(Long userId) {
        log.info("ActionLog.getUnreadCount.start userId {}", userId);
        long count = notificationRepository.countByUserEntityIdAndIsReadFalse(userId);
        log.info("ActionLog.getUnreadCount.end userId {} count {}", userId, count);
        return count;
    }
}
