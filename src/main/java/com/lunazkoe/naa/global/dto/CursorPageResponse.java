package com.lunazkoe.naa.global.dto;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> content,
        String nextCursor,
        String nextAfter,
        int size,
        Long totalElements,
        Boolean hasNext
) {

}
