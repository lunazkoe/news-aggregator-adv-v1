package com.lunazkoe.naa.domain.article.exception;

import com.lunazkoe.naa.global.error.CustomException;
import com.lunazkoe.naa.global.error.ErrorCode;
import java.util.Map;

public class ArticleException extends CustomException {

    public ArticleException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ArticleException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
