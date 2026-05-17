package com.lunazkoe.naa.domain.user.exception;

import com.lunazkoe.naa.global.error.CustomException;
import com.lunazkoe.naa.global.error.ErrorCode;
import java.util.Map;

public class UserException extends CustomException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
