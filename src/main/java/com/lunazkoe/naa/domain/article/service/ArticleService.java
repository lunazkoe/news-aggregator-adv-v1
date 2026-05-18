package com.lunazkoe.naa.domain.article.service;

import com.lunazkoe.naa.domain.article.dto.request.ArticleSearchCondition;
import com.lunazkoe.naa.domain.article.dto.response.ArticleDto;
import com.lunazkoe.naa.domain.article.dto.response.ArticleViewDto;
import com.lunazkoe.naa.domain.article.entity.Article;
import com.lunazkoe.naa.domain.article.entity.ArticleView;
import com.lunazkoe.naa.domain.article.exception.ArticleErrorCode;
import com.lunazkoe.naa.domain.article.exception.ArticleException;
import com.lunazkoe.naa.domain.article.repository.ArticleRepository;
import com.lunazkoe.naa.domain.article.repository.ArticleViewRepository;
import com.lunazkoe.naa.domain.user.entity.User;
import com.lunazkoe.naa.domain.user.exception.UserErrorCode;
import com.lunazkoe.naa.domain.user.exception.UserException;
import com.lunazkoe.naa.domain.user.repository.UserRepository;
import com.lunazkoe.naa.global.dto.CursorPageResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: count 관련 로직 동시성 문제

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleViewRepository articleViewRepository;

    /**
     * 기사 뷰 등록
     */
    @Transactional
    public ArticleViewDto registerArticleView(UUID articleId, UUID requestUserId) {
        Article foundArticle = getFoundArticleById(articleId);
        User foundUser = userRepository.getReferenceById(requestUserId);

        // 이미 조회했다면 그냥 원래 조회수 정보를 반환하고 종룐
        Optional<ArticleView> foundArticleViewWithArticle = articleViewRepository.findByArticleIdAndUserIdWithArticle(
                articleId, requestUserId);
        if (foundArticleViewWithArticle.isPresent()) {
            return ArticleViewDto.from(foundArticleViewWithArticle.get());
        }

        // 조회 엔티티 생성
        ArticleView newArticleView = new ArticleView(foundArticle, foundUser);
        ArticleView savedArticleView = articleViewRepository.save(newArticleView);

        // 조회수 증가
        foundArticle.increaseViewCount();

        log.info("기사 조회 성공. ArticleId: {}", foundArticle.getId());
        return ArticleViewDto.from(savedArticleView);
        // - 이미 foundArticle이 들어가 있어서 조회 시 쿼리가 나가지 않음
    }

    /**
     * 뉴스 기사 목록 조회
     */
    @Transactional(readOnly = true)
    public CursorPageResponse<ArticleDto> getArticles(ArticleSearchCondition condition,
            UUID requestUserid) {
        CursorPageResponse<Article> pageResponse = articleRepository.searchArticles(
                condition);

        if (pageResponse.content().isEmpty()) {
            return new CursorPageResponse<>(
                    Collections.emptyList(),
                    pageResponse.nextCursor(),
                    pageResponse.nextAfter(),
                    pageResponse.size(),
                    pageResponse.totalElements(),
                    pageResponse.hasNext()
            );
        }

        List<UUID> articleIds = pageResponse.content().stream()
                .map(Article::getId)
                .toList();
        Set<UUID> viewedArticleIds = articleViewRepository.findViewedArticleIds(requestUserid,
                articleIds);

        // N + 1 문제 해결?
        List<ArticleDto> articles = pageResponse.content().stream()
                .map(article -> {
                    boolean viewedByMe = viewedArticleIds.contains(article.getId());
                    return ArticleDto.from(article, viewedByMe);
                })
                .toList();

        log.info("뉴스 기사 목록 조회 성공.");

        return new CursorPageResponse<>(
                articles,
                pageResponse.nextCursor(),
                pageResponse.nextAfter(),
                pageResponse.size(),
                pageResponse.totalElements(),
                pageResponse.hasNext()
        );
    }

    /**
     * 뉴스 기사 단건 조회
     */
    @Transactional(readOnly = true)
    public ArticleDto getArticle(UUID articleId, UUID requestUserId) {
        Article foundArticle = getFoundArticleById(articleId);

        boolean viewedByMe = articleViewRepository.existsByArticleIdAndUserIdDirectly(articleId,
                requestUserId);

        log.info("뉴스 기사 단건 조회 완료. ArticleId: {}", foundArticle.getId());
        return ArticleDto.from(foundArticle, viewedByMe);
    }

    /**
     * 뉴스 기사 논리 삭제
     */
    @Transactional
    public void softDelete(UUID articleId) {
        Article foundArticle = getFoundArticleById(articleId);

        // TODO: 논리 삭제 시 연관관계 삭제 구현
        foundArticle.softDelete();

        log.info("뉴스 기사 논리 삭제 완료. ArticleId: {}", foundArticle.getId());
    }

    /**
     * 출처 목록 조회 - 그냥 컨트롤러 단에서 호출
     */

    /**
     * 뉴스 복구
     */

    /**
     * 뉴스 기사 물리 삭제
     */
    @Transactional
    public void hardDelete(UUID articleId) {
        Article foundArticle = getFoundArticleById(articleId);

        // TODO: 물리 삭제 시 연관관계 삭제 구현
        articleRepository.delete(foundArticle);

        log.info("뉴스 기사 물리 삭제 완료. ArticleId: {}", foundArticle.getId());
    }

    private Article getFoundArticleById(UUID articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleException(ArticleErrorCode.ARTICLE_NOT_FOUND));
    }

    private User getFoundUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
