package com.lunazkoe.naa.domain.article.repository;

import static com.lunazkoe.naa.domain.article.entity.QArticle.article;

import com.lunazkoe.naa.domain.article.dto.request.ArticleSearchCondition;
import com.lunazkoe.naa.domain.article.entity.Article;
import com.lunazkoe.naa.domain.article.entity.Source;
import com.lunazkoe.naa.global.dto.CursorPageResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

// TODO: interestId로 뭔가를 하는 부분이 없음 => 이 부분 필요함

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponse<Article> searchArticles(ArticleSearchCondition condition) {

        int limit = condition.limit();
        List<Article> articles = queryFactory
                .selectFrom(article)
                .where(
                        isNotDeleted(),
                        containsKeyword(condition.keyword()),
                        sourceIn(condition.sourceIn()),
                        publishDateBetween(condition.publishDateFrom(), condition.publishDateTo()),
                        cursorCondition(condition.orderBy(), condition.direction(),
                                condition.cursor(), condition.after())
                )
                .orderBy(createOrderSpecifier(condition.orderBy(), condition.direction()))
                .limit(limit + 1)
                .fetch();

        boolean hasNext = articles.size() > limit;
        String nextCursor = null;
        String nextAfter = null;

        if (hasNext) {
            articles.remove(articles.size() - 1);

            Article lastArticle = articles.get(articles.size() - 1);
            nextCursor = lastArticle.getId().toString();
            nextAfter = switch (condition.orderBy() != null ? condition.orderBy() : "publishDate") {
                case "commentCount" -> String.valueOf(lastArticle.getCommentCount());
                case "viewCount" -> String.valueOf(lastArticle.getViewCount());
                default -> lastArticle.getPublishDate().toString();
            };
        }

        Long totalElementsCount = null;
        if (!StringUtils.hasText(condition.cursor())) {
            totalElementsCount = Optional.ofNullable(
                    queryFactory
                            .select(article.count())
                            .from(article)
                            .where(
                                    isNotDeleted(),
                                    containsKeyword(condition.keyword()),
                                    sourceIn(condition.sourceIn()),
                                    publishDateBetween(condition.publishDateFrom(),
                                            condition.publishDateTo())
                            )
                            .fetchOne()
            ).orElse(0L);
        }

        return new CursorPageResponse<>(
                articles,
                nextCursor,
                nextAfter,
                condition.limit(),
                totalElementsCount,
                hasNext
        );
    }

    private BooleanExpression isNotDeleted() {
        return article.isDeleted.eq(false);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return article.title.containsIgnoreCase(keyword)
                .or(article.summary.containsIgnoreCase(keyword));
        // TODO: TEXT에 %keyword%를 하게 되면 인덱스를 타지 못하고 풀 스캔 -> 그냥 성능 이슈가 있을 수 있음 인지
    }

    private BooleanExpression sourceIn(List<Source> sourceIn) {
        if (sourceIn == null || sourceIn.isEmpty()) {
            // => 1=0이라는 표현 항상 false를 반환하게 하여 아무것도 조회하지 않게 할 수 있음
            return Expressions.asBoolean(true).isFalse();
        }
        return article.source.in(sourceIn);
    }

    private BooleanExpression publishDateBetween(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null) {
            return article.publishDate.between(from, to);
        } else if (from != null) {
            return article.publishDate.goe(from);
        } else if (to != null) {
            return article.publishDate.loe(to);
        }
        return null;
    }

    private BooleanExpression cursorCondition(String orderBy, String direction, String cursor,
            String after) {
        if (!StringUtils.hasText(cursor)) {
            return null;
        }

        UUID cursorId = UUID.fromString(cursor);
        boolean isAsc = "ASC".equalsIgnoreCase(direction);

        return switch (orderBy != null ? orderBy : "publishDate") {
            case "commentCount" -> {
                // 클라이언트가 after를 보내준 경우
                if (StringUtils.hasText(after)) {
                    long afterCommentCount = Long.parseLong(after);
                    yield isAsc ?
                            article.commentCount.gt(afterCommentCount)
                            .or(article.commentCount.eq(afterCommentCount)
                                .and(article.id.gt(cursorId))) :
                            article.commentCount.lt(afterCommentCount)
                            .or(article.commentCount.eq(afterCommentCount)
                                .and(article.id.lt(cursorId)));
                } else {
                    // 클라이언트가 null을 보낸 경우: 서버가 알아서 DB 서브쿼리로 값을 찾아옴 (Fallback)
                    var subQuery = JPAExpressions.select(article.commentCount).from(article)
                            .where(article.id.eq(cursorId));
                    yield isAsc ?
                            article.commentCount.gt(subQuery)
                            .or(article.commentCount.eq(subQuery).and(article.id.gt(cursorId))) :
                            article.commentCount.lt(subQuery)
                            .or(article.commentCount.eq(subQuery).and(article.id.lt(cursorId)));
                }
            }

            case "viewCount" -> {
                if (StringUtils.hasText(after)) {
                    // after 값이 있는 경우
                    long afterViewCount = Long.parseLong(after);
                    yield isAsc ?
                            article.viewCount.gt(afterViewCount)
                            .or(article.viewCount.eq(afterViewCount).and(article.id.gt(cursorId))) :
                            article.viewCount.lt(afterViewCount)
                            .or(article.viewCount.eq(afterViewCount).and(article.id.lt(cursorId)));
                } else {
                    // after 값이 없는 경우 (null)
                    var subQuery = JPAExpressions.select(article.viewCount).from(article)
                            .where(article.id.eq(cursorId));
                    yield isAsc ?
                            article.viewCount.gt(subQuery)
                            .or(article.viewCount.eq(subQuery).and(article.id.gt(cursorId))) :
                            article.viewCount.lt(subQuery)
                            .or(article.viewCount.eq(subQuery).and(article.id.lt(cursorId)));
                }
            }

            // 기본값: publishDate 정렬
            default -> {
                if (StringUtils.hasText(after)) {
                    LocalDateTime afterDate;
                    try {
                        afterDate = ZonedDateTime.parse(after).toLocalDateTime();
                    } catch (Exception e) {
                        afterDate = LocalDateTime.parse(after);
                    }
                    yield isAsc ?
                            article.publishDate.gt(afterDate)
                            .or(article.publishDate.eq(afterDate).and(article.id.gt(cursorId))) :
                            article.publishDate.lt(afterDate)
                            .or(article.publishDate.eq(afterDate).and(article.id.lt(cursorId)));
                } else {
                    // 프론트엔드가 after 파라미터를 빼먹고 보냈을 때의 강력한 Fallback 로직
                    var subQuery = JPAExpressions.select(article.publishDate).from(article)
                            .where(article.id.eq(cursorId));
                    yield isAsc ?
                            article.publishDate.gt(subQuery)
                            .or(article.publishDate.eq(subQuery).and(article.id.gt(cursorId))) :
                            article.publishDate.lt(subQuery)
                            .or(article.publishDate.eq(subQuery).and(article.id.lt(cursorId)));
                }
            }
        };
    }

    // 동적 정렬 로직 (반드시 cursorCondition의 논리와 짝을 이루어야 함)
    private OrderSpecifier<?>[] createOrderSpecifier(String orderBy, String direction) {
        Order orderDirection = "ASC".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;

        // 주 정렬 조건이 같을 경우(동점자 발생), 반드시 고유값인 id를 보조 정렬 조건으로 추가해야 함
        return switch (orderBy != null ? orderBy : "publishDate") {
            case "commentCount" ->
                    new OrderSpecifier[]{new OrderSpecifier<>(orderDirection, article.commentCount),
                            new OrderSpecifier<>(orderDirection, article.id)};
            case "viewCount" ->
                    new OrderSpecifier[]{new OrderSpecifier<>(orderDirection, article.viewCount),
                            new OrderSpecifier<>(orderDirection, article.id)};
            default ->
                    new OrderSpecifier[]{new OrderSpecifier<>(orderDirection, article.publishDate),
                            new OrderSpecifier<>(orderDirection, article.id)};
        };
    }
}
