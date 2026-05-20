package com.lunazkoe.naa.domain.notification.service;

import com.lunazkoe.naa.domain.notification.dto.request.NotificationSearchCondition;
import com.lunazkoe.naa.domain.notification.dto.response.NotificationDto;
import com.lunazkoe.naa.domain.notification.entity.Notification;
import com.lunazkoe.naa.domain.notification.exception.NotificationErrorCode;
import com.lunazkoe.naa.domain.notification.exception.NotificationException;
import com.lunazkoe.naa.domain.notification.repository.NotificationRepository;
import com.lunazkoe.naa.domain.user.exception.UserErrorCode;
import com.lunazkoe.naa.domain.user.exception.UserException;
import com.lunazkoe.naa.domain.user.repository.UserRepository;
import com.lunazkoe.naa.global.dto.CursorPageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public CursorPageResponse<NotificationDto> getNotifications(
            NotificationSearchCondition condition, UUID requestUserId) {
        CursorPageResponse<Notification> pageResponse = notificationRepository.searchNotifications(
                condition, requestUserId);

        List<NotificationDto> notifications = pageResponse.content().stream()
                .map(NotificationDto::from)
                .toList();

        log.info("미확인 알림 목록 조회");
        return new CursorPageResponse<>(
                notifications,
                pageResponse.nextCursor(),
                pageResponse.nextAfter(),
                pageResponse.size(),
                pageResponse.totalElements(),
                pageResponse.hasNext()
        );
    }

    /**
     * 전체 알림 확인
     */
    @Transactional
    public void confirmAllNotifications(UUID requestUserId) {

        userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        int rowCount = notificationRepository.confirmAllByUserId(requestUserId,
                LocalDateTime.now());
        log.info("전체 알림 {}건 확인 요청 완료. UserId: {}", rowCount, requestUserId);
    }

    /**
     * 알림 확인
     */
    @Transactional
    public void confirmNotification(UUID notificationId, UUID requestUserId) {
        Notification foundNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(
                        NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (!foundNotification.getUser().getId().equals(requestUserId)) {
            throw new NotificationException(NotificationErrorCode.FORBIDDEN_ACCESS);
        }

        // TODO: 동시성 문제
        foundNotification.confirm();

        log.info("알림 확인 요청 완료. UserId: {}", requestUserId);
    }
}
