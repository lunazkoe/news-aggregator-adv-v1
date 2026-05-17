package com.lunazkoe.naa.domain.interest.service;

import com.lunazkoe.naa.domain.interest.dto.request.InterestRegisterRequest;
import com.lunazkoe.naa.domain.interest.dto.request.InterestUpdateRequest;
import com.lunazkoe.naa.domain.interest.dto.response.InterestDto;
import com.lunazkoe.naa.domain.interest.entity.Interest;
import com.lunazkoe.naa.domain.interest.exception.InterestErrorCode;
import com.lunazkoe.naa.domain.interest.exception.InterestException;
import com.lunazkoe.naa.domain.interest.repository.InterestRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;

    /**
     * 관심사 목록 조회
     */

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

    /**
     * 관심사 구독 취소
     */

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
    }

    private Interest getFoundInterestById(UUID interestId) {
        return interestRepository.findById(interestId)
                .orElseThrow(() -> new InterestException(InterestErrorCode.INTEREST_NOT_FOUND));
    }
}
