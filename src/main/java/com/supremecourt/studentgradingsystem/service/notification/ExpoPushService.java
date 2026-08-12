package com.supremecourt.studentgradingsystem.service.notification;

import com.supremecourt.studentgradingsystem.dao.repository.DeviceTokenRepository;
import com.supremecourt.studentgradingsystem.model.response.Note;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpoPushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final DeviceTokenRepository deviceTokenRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public void sendNotification(Note note, String token) {
        if (token == null || token.isBlank()) {
            log.warn("ActionLog.sendNotification.emptyToken");
            return;
        }

        // Check if it's actually an Expo token
        if (!token.startsWith("ExponentPushToken[")) {
            log.warn("ActionLog.sendNotification.notExpoToken token {}", token);
            // Still try, maybe it's an FCM token from a non-Expo build
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "to", token,
                "title", note.getSubject(),
                "body", note.getContent(),
                "data", Map.of("type", "notification")
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<ExpoPushResponse> response = restTemplate.postForEntity(
                    EXPO_PUSH_URL, request, ExpoPushResponse.class);

            if (response.getBody() != null && response.getBody().data != null) {
                for (ExpoPushTicket ticket : response.getBody().data) {
                    if ("error".equals(ticket.status)) {
                        if (isInvalidTokenError(ticket.details)) {
                            log.warn("ActionLog.sendNotification.invalidExpoToken token {} error {}",
                                    token, ticket.message);
                            deviceTokenRepository.deleteByToken(token);
                        } else {
                            log.error("ActionLog.sendNotification.expoError token {} error {} details {}",
                                    token, ticket.message, ticket.details);
                        }
                    } else {
                        log.info("ActionLog.sendNotification.expoSuccess token {} ticketId {}",
                                token, ticket.id);
                    }
                }
            }
        } catch (Exception e) {
            log.error("ActionLog.sendNotification.expoFailed token {} error {}", token, e.getMessage());
        }
    }

    private boolean isInvalidTokenError(ExpoPushErrorDetails details) {
        if (details == null || details.error == null) return false;
        return "DeviceNotRegistered".equals(details.error)
                || "InvalidCredentials".equals(details.error);
    }

    // Inner DTOs for Expo API response
    public static class ExpoPushResponse {
        public List<ExpoPushTicket> data;
    }

    public static class ExpoPushTicket {
        public String status;
        public String id;
        public String message;
        public ExpoPushErrorDetails details;
    }

    public static class ExpoPushErrorDetails {
        public String error;
    }
}
