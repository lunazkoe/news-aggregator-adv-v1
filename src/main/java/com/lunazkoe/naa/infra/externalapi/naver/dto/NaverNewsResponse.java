package com.lunazkoe.naa.infra.externalapi.naver.dto;

import java.util.List;

public record NaverNewsResponse(
        String lastBuildDate,
        int total,
        int start,
        int display,
        List<NaverNewsItem> items
) {

}
