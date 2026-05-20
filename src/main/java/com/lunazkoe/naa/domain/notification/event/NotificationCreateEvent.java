package com.lunazkoe.naa.domain.notification.event;

import com.lunazkoe.naa.domain.notification.entity.ResourceType;
import java.util.UUID;

public record NotificationCreateEvent(
        UUID receiverId,
        String content,
        ResourceType resourceType,
        UUID resourceId
) {

}
