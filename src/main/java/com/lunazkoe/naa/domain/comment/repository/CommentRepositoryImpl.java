package com.lunazkoe.naa.domain.comment.repository;

import static com.lunazkoe.naa.domain.comment.entity.QComment.comment;
import static com.lunazkoe.naa.domain.user.entity.QUser.user;

import com.lunazkoe.naa.domain.comment.dto.request.CommentSearchCondition;
import com.lunazkoe.naa.domain.comment.entity.Comment;
import com.lunazkoe.naa.global.dto.CursorPageResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponse<Comment> searchComments(CommentSearchCondition condition) {

        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .join(comment.user, user).fetchJoin() // DTO 변환시 User를 다시 select하는 N+1 문제 발생!! 대비
                .where(
                        isNotDeleted(),
                        searchArticleId(condition.articleId()),
                        cursorCondition(condition.orderBy(), condition.direction(),
                                condition.cursor(), condition.after())
                )
                .orderBy(createOrderSpecifier(condition.orderBy(), condition.direction()))
                .limit(condition.limit() + 1)
                .fetch();

        boolean hasNext = comments.size() > condition.limit();
        String nextCursor = null;
        String nextAfter = null;

        if (hasNext) {
            comments.remove(comments.size() - 1);

            Comment lastComment = comments.get(comments.size() - 1);
            nextCursor = lastComment.getId().toString();
            nextAfter = switch (condition.orderBy() != null ? condition.orderBy() : "createdAt") {
                case "likeCount" -> String.valueOf(lastComment.getLikeCount());
                default -> lastComment.getCreatedAt().toString();
            };
        }

        Long totalElementsCount = null;
        if (!StringUtils.hasText(condition.cursor())) {
            totalElementsCount = Optional.ofNullable(
                    queryFactory
                            .select(comment.count())
                            .from(comment)
                            .where(
                                    isNotDeleted(),
                                    searchArticleId(condition.articleId())
                            )
                            .fetchOne()
            ).orElse(0L);
        }

        return new CursorPageResponse<>(
                comments,
                nextCursor,
                nextAfter,
                condition.limit(),
                totalElementsCount,
                hasNext
        );
    }

    private BooleanExpression isNotDeleted() {
        return comment.isDeleted.eq(false);
    }

    private BooleanExpression searchArticleId(UUID articleId) {
//        if (articleId == null) {
//            return Expressions.asBoolean(true).isFalse();
//            // - 특정 기사에 대한 ID를 주지 않으면 검색을 할 수 없음
//        } => 이거 그냥 Service 단에서 처리하도록!!
        return comment.article.id.eq(articleId);
    }

    private BooleanExpression cursorCondition(String orderBy, String direction, String cursor,
            String after) {
        if (!StringUtils.hasText(cursor)) {
            return null;
        }

        UUID cursorId = UUID.fromString(cursor);
        // - 이게 본래 의도는 cursor가 UUID 형식이 아니면 여기서 에러나게 하는건데..
        boolean isAsc = "ASC".equalsIgnoreCase(direction);

        return switch (orderBy != null ? orderBy : "createdAt") {
            case "likeCount" -> {
                if (StringUtils.hasText(after)) {
                    long afterLikeCount = Long.parseLong(after);
                    yield isAsc ?
                            comment.likeCount.gt(afterLikeCount)
                            .or(comment.likeCount.eq(afterLikeCount).and(comment.id.gt(cursorId))) :
                            comment.likeCount.lt(afterLikeCount)
                            .or(comment.likeCount.eq(afterLikeCount).and(comment.id.lt(cursorId)));
                } else {
                    var subQuery = JPAExpressions.select(comment.likeCount).from(comment)
                            .where(comment.id.eq(cursorId));
                    yield isAsc ?
                            comment.likeCount.gt(subQuery)
                            .or(comment.likeCount.eq(subQuery).and(comment.id.gt(cursorId))) :
                            comment.likeCount.lt(subQuery)
                            .or(comment.likeCount.eq(subQuery).and(comment.id.lt(cursorId)));
                }
            }

            default -> {
                if (StringUtils.hasText(after)) {
                    LocalDateTime afterDate;
                    try {
                        afterDate = ZonedDateTime.parse(after).toLocalDateTime();
                    } catch (DateTimeParseException e) {
                        afterDate = LocalDateTime.parse(after);
                    }
                    yield isAsc ?
                            comment.createdAt.gt(afterDate)
                            .or(comment.createdAt.eq(afterDate).and(comment.id.gt(cursorId))) :
                            comment.createdAt.lt(afterDate)
                            .or(comment.createdAt.eq(afterDate).and(comment.id.lt(cursorId)));

                } else {
                    var subquery = JPAExpressions.select(comment.createdAt).from(comment)
                            .where(comment.id.eq(cursorId));
                    yield isAsc ?
                            comment.createdAt.gt(subquery)
                            .or(comment.createdAt.eq(subquery).and(comment.id.gt(cursorId))) :
                            comment.createdAt.lt(subquery)
                            .or(comment.createdAt.eq(subquery).and(comment.id.lt(cursorId)));
                }
            }
        };
    }

    private OrderSpecifier<?>[] createOrderSpecifier(String orderBy, String direction) {
        Order orderDirection = "ASC".equalsIgnoreCase(direction) ? Order.ASC : Order.DESC;

        return switch (orderBy != null ? orderBy : "createdAt") {
            case "likeCount" ->
                    new OrderSpecifier[]{new OrderSpecifier<>(orderDirection, comment.likeCount),
                            new OrderSpecifier<>(orderDirection, comment.id)};
            default -> new OrderSpecifier[]{new OrderSpecifier<>(orderDirection, comment.createdAt),
                    new OrderSpecifier<>(orderDirection, comment.id)};
        };
    }
}
