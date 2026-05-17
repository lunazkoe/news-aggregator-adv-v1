package com.lunazkoe.naa.domain.article.service;

import com.lunazkoe.naa.domain.article.dto.response.ArticleDto;
import com.lunazkoe.naa.domain.article.entity.Article;
import com.lunazkoe.naa.domain.article.exception.ArticleErrorCode;
import com.lunazkoe.naa.domain.article.exception.ArticleException;
import com.lunazkoe.naa.domain.article.repository.ArticleRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    /**
     * 기사 뷰 등록
     */

    /**
     * 뉴스 기사 목록 조회
     */

    /**
     * 뉴스 기사 단건 조회
     */
    @Transactional(readOnly = true)
    public ArticleDto getArticle(UUID articleId, UUID requestUserId) {
        Article foundArticle = getFoundArticleById(articleId);

        // TODO: viewedBy 구하기 - 현재는 임시로 false
        // - 또한 ViewCount 증가 처리는 여기서 안하고 따로 API를 만들 것 (기사 뷰 등록)

        return ArticleDto.from(foundArticle, false);
    }

    private Article getFoundArticleById(UUID articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleErrorCode.ARTICLE_NOT_FOUND));
    }
}
