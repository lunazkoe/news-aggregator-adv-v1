package com.lunazkoe.naa.domain.article.dto.request;

import com.lunazkoe.naa.domain.article.entity.Source;
import com.lunazkoe.naa.global.error.DomainException;
import com.lunazkoe.naa.global.error.GlobalErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ArticleSearchCondition(
        String keyword,                 // 검색어 (제목, 요약)
        UUID interestId,                // 관심사 필터
        List<Source> sourceIn,          // 출처 다중 필터 (NAVER, HANKYUNG 등)
        LocalDateTime publishDateFrom,  // 발행일 시작
        LocalDateTime publishDateTo,    // 발행일 끝

        @Pattern(regexp = "^(publishDate|commentCount|viewCount)$", message = "정렬 기준은 publishDate, commentCount, viewCount 중 하나여야 합니다.")
        String orderBy,                 // 정렬 기준

        @Schema(description = "정렬 방향 (ASC, DESC)", example = "DESC")
        @Pattern(regexp = "(?i)^(ASC|DESC)$", message = "정렬 방향은 ASC 또는 DESC만 가능합니다.")
        String direction,

        @Schema(description = "커서 값 (마지막으로 조회된 Comment ID)")
        @Pattern(regexp = "(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "유효하지 않은 커서 형식입니다.")
        String cursor,
        String after,                   // 보조 커서 (날짜, 정수 등)

        @Min(value = 1, message = "조회 개수는 1 이상이어야 합니다.")
        @Max(value = 100, message = "한 번에 조회할 수 있는 최대 개수는 100개입니다.")
        Integer limit                   // 페이지 크기 (int -> Integer 로 변경 추천)
) {

    // 파라미터가 비어있을 경우를 대비한 디폴트값 세팅
    public ArticleSearchCondition {
        if (limit == null || limit <= 0) {
            limit = 50;
        }
        if (orderBy == null || orderBy.isBlank()) {
            orderBy = "publishDate"; // 뉴스 기사에 맞는 기본 정렬
        }
        if (direction == null || direction.isBlank()) {
            direction = "DESC";
        }
        if (after != null && !after.isBlank()) {
            if ("commentCount".equals(orderBy) || "viewCount".equals(orderBy)) {
                try {
                    Long.parseLong(after);
                } catch (NumberFormatException e) {
                    // 숫자가 아니면 즉시 예외를 던져서 400 Bad Request 유도s
                    throw new DomainException(GlobalErrorCode.INVALID_INPUT_VALUE,
                            Map.of("after", "정렬 기준이 " + orderBy + "일 때 after 커서는 반드시 숫자여야 합니다."));
                }
            }
        }
    }
}
