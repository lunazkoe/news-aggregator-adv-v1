package com.lunazkoe.naa.domain.comment.exception;

import com.lunazkoe.naa.global.error.CustomException;
import com.lunazkoe.naa.global.error.ErrorCode;
import java.util.Map;

public class CommentException extends CustomException {

    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommentException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
