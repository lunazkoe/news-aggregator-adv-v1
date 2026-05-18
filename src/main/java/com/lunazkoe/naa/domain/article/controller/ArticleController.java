package com.lunazkoe.naa.domain.article.controller;

import static com.lunazkoe.naa.global.filter.MDCLoggingFilter.HEADER_USER_ID;

import com.lunazkoe.naa.domain.article.dto.request.ArticleSearchCondition;
import com.lunazkoe.naa.domain.article.dto.response.ArticleDto;
import com.lunazkoe.naa.domain.article.dto.response.ArticleViewDto;
import com.lunazkoe.naa.domain.article.entity.Source;
import com.lunazkoe.naa.domain.article.service.ArticleService;
import com.lunazkoe.naa.global.dto.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "기사 뷰 등록", description = "기사 뷰를 등록합니다.")
    @PostMapping("/{articleId}/article-views")
    @ResponseStatus(HttpStatus.OK)
    public ArticleViewDto registerArticleView(@PathVariable UUID articleId,
            @RequestHeader(HEADER_USER_ID) UUID requestUserId) {
        return articleService.registerArticleView(articleId, requestUserId);
    }

    @Operation(summary = "뉴스 기사 목록 조회", description = "조건에 맞는 뉴스 기사 목록을 조회합니다.")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public CursorPageResponse<ArticleDto> getArticles(
            @Valid @ModelAttribute ArticleSearchCondition condition,
            @RequestHeader(HEADER_USER_ID) UUID requestUserId) {
        return articleService.getArticles(condition, requestUserId);
    }

    @Operation(summary = "뉴스 기사 단건 조회", description = "뉴스 기사 ID로 뉴스 기사 단건을 조회합니다.")
    @GetMapping("/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto getArticle(@PathVariable UUID articleId,
            @RequestHeader(HEADER_USER_ID) UUID requestUserId) {
        return articleService.getArticle(articleId, requestUserId);
    }

    @Operation(summary = "뉴스 기사 논리 삭제", description = "뉴스 기사를 논리적으로 삭제합니다.")
    @DeleteMapping("/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@PathVariable UUID articleId) {
        articleService.softDelete(articleId);
    }

    @Operation(summary = "출처 목록 조회", description = "출처 목록을 조회합니다.")
    @GetMapping("/sources")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getSources() {
        return Arrays.stream(Source.values())
                .map(Enum::name)
                .toList();
    }

    @Operation(summary = "뉴스 기사 물리 삭제", description = "뉴스 기사를 물리적으로 삭제합니다.")
    @DeleteMapping("/{articleId}/hard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable UUID articleId) {
        articleService.hardDelete(articleId);
    }
}
