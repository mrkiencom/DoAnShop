package com.example.project.service;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private static final String TO_LECTURER = "To Lecturer";
    private static final String LOAD_COURSE = "Load Course";

    public void notifyAdmin(final int requestId, final String type, final String name, final String description, final int id) {
        final var text = handleMessage(type, name, description, id);
        final Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("sender", name);
        payload.put("message", text);
        payload.put("senderId", id);
        payload.put("requestId", requestId);
        payload.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/admin/notifications", payload);
    }

    private String handleMessage(final String type, final String name, final String description, final int id) {
        return switch (type) {
            case TO_LECTURER -> String.format("đã gửi yêu cầu muốn trở thành trợ giảng với nội dung: %s", description);
            case LOAD_COURSE -> String.format("đã gửi yêu cầu muốn đăng tải khóa học với nội dung: %s", description);

            default -> String.valueOf(type);
        };
    }
}

