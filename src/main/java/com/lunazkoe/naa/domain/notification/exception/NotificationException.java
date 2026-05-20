package com.lunazkoe.naa.domain.notification.exception;

import com.lunazkoe.naa.global.error.CustomException;
import com.lunazkoe.naa.global.error.ErrorCode;
import java.util.Map;

public class NotificationException extends CustomException {

    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotificationException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
