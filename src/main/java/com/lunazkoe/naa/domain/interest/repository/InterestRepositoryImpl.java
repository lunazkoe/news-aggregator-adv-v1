package com.lunazkoe.naa.domain.interest.repository;

import static com.lunazkoe.naa.domain.interest.entity.QInterest.interest;

import com.lunazkoe.naa.domain.interest.dto.request.InterestSearchCondition;
import com.lunazkoe.naa.domain.interest.entity.Interest;
import com.lunazkoe.naa.global.dto.CursorPageResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

// TODO: Subquery 불필요 문제. 클라이언트에서 명시적으로 무조건 보내면 어차피 실행안되서 상관은 없으나, 만약 그렇지 않다면 최적화가 따로 필요함

@Repository
@RequiredArgsConstructor
public class InterestRepositoryImpl implements InterestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponse<Interest> searchInterests(InterestSearchCondition condition) {
        List<Interest> interests = queryFactory
                .selectFrom(interest)
                .where(
                        containsKeyword(condition.keyword()),
                        cursorCondition(condition.orderBy(), condition.direction(),
                                condition.cursor(), condition.after())
                )
                .orderBy(dynamicOrder(condition.orderBy(), condition.direction()))
                .limit(condition.limit() + 1) // limit + 1로 hasNext를 판별하기 위함
                .fetch();

        boolean hasNext = interests.size() > condition.limit();
        String nextCursor = null;
        String nextAfter = null;

        if (hasNext) {
            interests.remove(interests.size() - 1); // 초과분 1개를 제거

            Interest lastInterest = interests.get(interests.size() - 1);
            nextCursor = lastInterest.getId().toString();
            nextAfter = switch (condition.orderBy() != null ? condition.orderBy() : "name") {
                case "subscriberCount" -> String.valueOf(lastInterest.getSubscriberCount());
                default -> lastInterest.getName();
            };
        }

        // cursor가 없을 때(첫 페이지 요청일 때만)
        // - 첫 페이지가 아닐 경우는 그냥 null로 보내서 처리 - 성능 최적화
        Long totalElementsCount = null;
        if (!StringUtils.hasText(condition.cursor())) {
            totalElementsCount = Optional.ofNullable(
                    queryFactory
                            .select(interest.count())
                            .from(interest)
                            .where(
                                    containsKeyword(condition.keyword())
                            )
                            .fetchOne()
            ).orElse(0L);
        }

        return new CursorPageResponse<>(
                interests,
                nextCursor,
                nextAfter,
                condition.limit(),
                totalElementsCount,
                hasNext
        );
    }

    // 동적 where 조건
    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        // 관심사 이름 중 키워드랑 매칭되는게 있는지 판별
        BooleanExpression nameMatch = interest.name.containsIgnoreCase(keyword);

        // 관심사에 등록된 키워드들 중 키워드랑 매칭되는게 있는지 판별
        StringTemplate keywordsAsString = Expressions.stringTemplate(
                "function('array_to_string', {0}, ',')", interest.keywords
        );
        BooleanExpression keywordMatch = keywordsAsString.containsIgnoreCase(keyword);

        return nameMatch.or(keywordMatch);
    }

    private BooleanExpression cursorCondition(String orderBy, String direction, String cursor,
            String after) {
        // 첫 페이지 요청이거나 커서 값이 없으면 조건 무시 (처음부터 조회)
        if (!StringUtils.hasText(cursor)) {
            return null;
        }

        UUID cursorId = UUID.fromString(cursor);
        boolean isAsc = "ASC".equalsIgnoreCase(direction);

        return switch (orderBy != null ? orderBy : "name") {
            case "subscriberCount" -> {
                if (StringUtils.hasText(after)) {
                    Long afterSubscriberCount = Long.parseLong(after);
                    yield isAsc ?
                            interest.subscriberCount.gt(afterSubscriberCount)
                            .or(interest.subscriberCount.eq(afterSubscriberCount)
                                .and(interest.id.gt(cursorId))) :
                            interest.subscriberCount.lt(afterSubscriberCount)
                            .or(interest.subscriberCount.eq(afterSubscriberCount)
                                .and(interest.id.lt(cursorId)));
                } else {
                    JPQLQuery<Long> subQuery = JPAExpressions.select(interest.subscriberCount)
                            .from(interest).where(interest.id.eq(cursorId));
                    yield isAsc ?
                            interest.subscriberCount.gt(subQuery)
                            .or(interest.subscriberCount.eq(subQuery).and(interest.id.gt(cursorId)))
                            :
                                    interest.subscriberCount.lt(subQuery)
                                    .or(interest.subscriberCount.eq(subQuery)
                                        .and(interest.id.lt(cursorId)));
                }
            }

            default -> {
                if (StringUtils.hasText(after)) {
                    yield isAsc ?
                            interest.name.gt(after)
                            .or(interest.name.eq(after).and(interest.id.gt(cursorId))) :
                            interest.name.lt(after)
                            .or(interest.name.eq(after).and(interest.id.lt(cursorId)));
                } else {
                    var subQuery = JPAExpressions.select(interest.name).from(interest)
                            .where(interest.id.eq(cursorId));
                    yield isAsc ?
                            interest.name.gt(subQuery)
                            .or(interest.name.eq(subQuery).and(interest.id.gt(cursorId))) :
                            interest.name.lt(subQuery)
                            .or(interest.name.eq(subQuery).and(interest.id.lt(cursorId)));
                }
            }
        };
    }

    private OrderSpecifier<?>[] dynamicOrder(String orderBy, String direction) {
        boolean isASC = "ASC".equalsIgnoreCase(direction);

        if ("subscriberCount".equalsIgnoreCase(orderBy)) {
            return new OrderSpecifier[]{
                    isASC ? interest.subscriberCount.asc() : interest.subscriberCount.desc(),
                    isASC ? interest.id.asc() : interest.id.desc()
            };
        } else {
            return new OrderSpecifier[]{
                    isASC ? interest.name.asc() : interest.name.desc(),
                    isASC ? interest.id.asc() : interest.id.desc()
            };
        }
    }
}
