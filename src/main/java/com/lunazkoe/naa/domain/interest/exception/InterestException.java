package com.lunazkoe.naa.domain.interest.exception;

import com.lunazkoe.naa.global.error.CustomException;
import com.lunazkoe.naa.global.error.ErrorCode;
import java.util.Map;

public class InterestException extends CustomException {

    public InterestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InterestException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
