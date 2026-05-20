package com.lunazkoe.naa.domain.notification.dto.response;

import com.lunazkoe.naa.domain.notification.entity.Notification;
import com.lunazkoe.naa.domain.notification.entity.ResourceType;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean confirmed,
        UUID userId,
        String content,
        ResourceType resourceType,
        UUID resourceId
) {

    public static NotificationDto from(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                notification.isConfirmed(),
                notification.getUser().getId(),
                notification.getContent(),
                notification.getResourceType(),
                notification.getResourceId()
        );
    }
}
