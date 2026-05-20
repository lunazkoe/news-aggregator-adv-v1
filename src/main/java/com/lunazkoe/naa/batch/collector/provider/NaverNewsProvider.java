package com.lunazkoe.naa.batch.collector.provider;

import com.lunazkoe.naa.domain.article.entity.Source;
import com.lunazkoe.naa.domain.interest.entity.Interest;
import com.lunazkoe.naa.infra.externalapi.naver.client.NaverNewsClient;
import com.lunazkoe.naa.infra.externalapi.naver.dto.NaverNewsResponse;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverNewsProvider implements NewsProvider {

    private final NaverNewsClient naverNewsClient;
    // 네이버 날짜 포맷 파싱용
    private static final DateTimeFormatter NAVER_DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;

    @Value("${external-api.naver.client-id}")
    private String naverClientId;

    @Value("${external-api.naver.client-secret}")
    private String naverClientSecret;

    @Override
    public List<CollectedNewsDto> fetchNews(Interest interest) {
        List<String> keywords = interest.getKeywords();
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }

        // 키워드들을 OR로 묶어서 query string 만들기
        String combinedQuery = keywords.stream()
                .filter(k -> k != null && !k.isBlank())
                .collect(Collectors.joining(" OR "));

        if (combinedQuery.isBlank()) {
            return Collections.emptyList();
        }

        try {
            NaverNewsResponse response = naverNewsClient.searchNews(
                    naverClientId, naverClientSecret, combinedQuery, 10, 1, "date");

            if (response == null || response.items() == null) {
                return Collections.emptyList();
            }

            return response.items().stream()
                    .filter(item -> item.originallink() != null && !item.originallink().isBlank())
                    .map(item -> new CollectedNewsDto(
                            getSource(),
                            item.originallink(),
                            stripHtmlTags(item.title()),
                            parsePubDate(item.pubDate()),
                            stripHtmlTags(item.description())
                    ))
                    .toList();
        } catch (Exception e) {
            log.error("[NaverNewsProvider] 뉴스 수집 중 예외 발생. 관심사 ID: {}, 원인: {}", interest.getId(),
                    e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Source getSource() {
        return Source.NAVER;
    }

    private String stripHtmlTags(String text) {
        if (text == null) {
            return "";
        }
        String noTagText = text.replaceAll("<[^>]*>", "");
        return HtmlUtils.htmlUnescape(noTagText);
    }

    private LocalDateTime parsePubDate(String pubDateStr) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDateStr, NAVER_DATE_FORMATTER);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
