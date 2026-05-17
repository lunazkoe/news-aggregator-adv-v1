package com.lunazkoe.naa.domain.article.controller;

import static com.lunazkoe.naa.global.filter.MDCLoggingFilter.HEADER_USER_ID;

import com.lunazkoe.naa.domain.article.dto.response.ArticleDto;
import com.lunazkoe.naa.domain.article.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "뉴스 기사 단건 조회", description = "뉴스 기사 ID로 뉴스 기사 단건을 조회합니다.")
    @GetMapping("/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleDto getArticle(@PathVariable UUID articleId,
            @RequestHeader(HEADER_USER_ID) UUID requestUserId) {
        return articleService.getArticle(articleId, requestUserId);
    }
}
