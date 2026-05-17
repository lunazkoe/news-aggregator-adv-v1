package com.lunazkoe.naa.domain.interest.service;

import com.lunazkoe.naa.domain.interest.dto.request.InterestRegisterRequest;
import com.lunazkoe.naa.domain.interest.dto.request.InterestSearchCondition;
import com.lunazkoe.naa.domain.interest.dto.request.InterestUpdateRequest;
import com.lunazkoe.naa.domain.interest.dto.response.InterestDto;
import com.lunazkoe.naa.domain.interest.dto.response.SubscriptionDto;
import com.lunazkoe.naa.domain.interest.entity.Interest;
import com.lunazkoe.naa.domain.interest.entity.Subscription;
import com.lunazkoe.naa.domain.interest.exception.InterestErrorCode;
import com.lunazkoe.naa.domain.interest.exception.InterestException;
import com.lunazkoe.naa.domain.interest.repository.InterestRepository;
import com.lunazkoe.naa.domain.interest.repository.SubscriptionRepository;
import com.lunazkoe.naa.domain.user.entity.User;
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

// TODO: update / increase / decrease 동시성 문제
// TODO: 연관관계 삭제(물리 삭제 시)

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    /**
     * 관심사 목록 조회
     */
    @Transactional(readOnly = true)
    public CursorPageResponse<InterestDto> searchInterests(InterestSearchCondition condition,
            UUID requestUserId) {
        CursorPageResponse<Interest> pageResponse = interestRepository.searchInterests(
                condition);

        // 아무것도 없는 방어로직을 짜는 이유
        // - 물어볼 ID가 없는데 DB에 존재하는지 물어보는 것 자체가 DB 네트워크 낭비
        // - where id () 처럼 괄호가 비어있는 문법을 허용하지 않음 (최근에는 Hibernate가 알아서 처리해주긴함)
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

        // N+1 문제 발생!!
        // - 조회는 한 번에 했는데(1), 본인의 구독 여부를 판별하는게 관심사 별로 하나씩 나감(N) => N+1
        // - 해결방안: 조회해온 interests에 대해서 구독 여부를 한 번에 가져옴
        List<UUID> interestIds = pageResponse.content().stream()
                .map(Interest::getId)
                .toList();

        Set<UUID> subscribedInterestIds = subscriptionRepository.findSubscribedInterestIdsByUserIdAndInterestIds(
                requestUserId, interestIds);

        List<InterestDto> interests = pageResponse.content().stream()
                .map(interest -> {
                    boolean subscribedByMe = subscribedInterestIds.contains(interest.getId());
//                    boolean subscribedByMe = subscriptionRepository.existsByInterestIdAndUserId(
//                            interest.getId(), requestUserId);
//                    boolean subscribedByMe = subscriptionRepository.existsInterestIdAndUserId(
//                            interest.getId(), requestUserId);
                    return InterestDto.from(interest, subscribedByMe);
                })
                .toList();

        log.info("관심사 목록 조회 성공.");

        return new CursorPageResponse<>(
                interests,
                pageResponse.nextCursor(),
                pageResponse.nextAfter(),
                pageResponse.size(),
                pageResponse.totalElements(),
                pageResponse.hasNext()
        );
    }

    /**
     * 관심사 등록
     */
    @Transactional
    public InterestDto createInterest(InterestRegisterRequest request) {
        // TODO: 유사 관심사 중복 처리 - 현재는 일부러 중복 허용

        Interest newInterest = new Interest(request.name(), request.keywords());
        Interest savedInterest = interestRepository.save(newInterest);

        log.info("관심사 등록 완료. InterestId: {}", savedInterest.getId());
        return InterestDto.from(savedInterest, false);
        // - 관심사를 등록을 했을 경우 무조건 처음에는 구독이 되어있지 않으므로 false
    }

    /**
     * 관심사 구독
     */
    @Transactional
    public SubscriptionDto createSubscription(UUID interestId, UUID requestUserId) {
        // 이미 구독 중인지 확인
        Optional<Subscription> foundSubscription = subscriptionRepository.findByInterestIdAndUserIdWithInterest(
                interestId, requestUserId);

        if (foundSubscription.isPresent()) {
            // 이미 구독 중이라면 원래 구독 정보를 그냥 반환
            // - Interest를 같이 가져와서 쿼리 최적화
            return SubscriptionDto.from(foundSubscription.get());
        }

        // 새로운 구독
        Interest foundInterest = getFoundInterestById(interestId);
        User proxyUser = userRepository.getReferenceById(requestUserId);
        // - DB 쿼리가 발생하지 않음. ID만 가진 껍데기
//        User foundUser = getFoundUserById(requestUserId);

        Subscription newSubscription = new Subscription(foundInterest, proxyUser);
        Subscription savedSubscription = subscriptionRepository.save(newSubscription);

        // 구독자 수 증가
        foundInterest.increaseSubscriberCount(); // 더티 체크로 같이 업데이트 됨

        log.info("관심사 구독 완료. SubscriptionId: {}", savedSubscription.getId());
        return SubscriptionDto.from(savedSubscription);
    }

    /**
     * 관심사 구독 취소
     */
    @Transactional
    public void cancelSubscription(UUID interestId, UUID requestUserId) {
        // 구독 중인지 확인
        Optional<Subscription> foundSubscription = subscriptionRepository.findByInterestIdAndUserIdWithInterest(
                interestId, requestUserId);

        if (foundSubscription.isEmpty()) {
            // 구독을 하지 않았다면 그냥 종료
            return;
        }

        // 구독 취소
        subscriptionRepository.delete(foundSubscription.get());

        // 구독자 수 감소
        Interest foundInterest = foundSubscription.get().getInterest();
        foundInterest.decreaseSubscriberCount();

        log.info("관심사 구독 취소 완료. SubscriptionId: {}", foundSubscription.get().getId());
    }

    /**
     * 관심사 물리 삭제
     */
    @Transactional
    public void hardDelete(UUID interestId) {

        Interest foundInterest = getFoundInterestById(interestId);

        // TODO: 물리 삭제 시 연관관계들에서 일어날 일들 처리

        interestRepository.delete(foundInterest);
        log.info("관심사 물리 삭제 완료. InterestId: {}", foundInterest.getId());
    }

    /**
     * 관심사 정보 수정
     */
    @Transactional
    public InterestDto updateInterestKeywords(UUID interestId, InterestUpdateRequest request) {
        Interest foundInterest = getFoundInterestById(interestId);
        foundInterest.updateKeywords(request.keywords());
        log.info("관심사 정보(키워드) 수정 완료. InterestId: {}", foundInterest.getId());
        return InterestDto.from(foundInterest, false);
        // TODO: 나에 의한 구독 정보는 구독 관련 작업 후 진행
        // - 현재 구조에서의 문제점: requestUserId를 받지 않아서 이걸 어떻게 처리할지 논의가 필요함
        // - 유저 활동 내역서에서 가져오거나 해야할듯
    }

    private Interest getFoundInterestById(UUID interestId) {
        return interestRepository.findById(interestId)
                .orElseThrow(() -> new InterestException(InterestErrorCode.INTEREST_NOT_FOUND));
    }
}
