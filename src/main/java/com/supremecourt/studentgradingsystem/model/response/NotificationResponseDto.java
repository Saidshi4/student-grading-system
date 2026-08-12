package com.supremecourt.studentgradingsystem.model.response;

import com.supremecourt.studentgradingsystem.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private String title;
    private String description;
    private String content;
    private Boolean isRead;
    private NotificationType type;
    private Long relatedId;
    private Instant createdAt;
}