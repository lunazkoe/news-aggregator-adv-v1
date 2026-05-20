package com.lunazkoe.naa.domain.notification.repository;

import com.lunazkoe.naa.domain.notification.dto.request.NotificationSearchCondition;
import com.lunazkoe.naa.domain.notification.entity.Notification;
import com.lunazkoe.naa.global.dto.CursorPageResponse;
import java.util.UUID;

public interface NotificationRepositoryCustom {

    CursorPageResponse<Notification> searchNotifications(NotificationSearchCondition condition,
            UUID requestUserId);
}
