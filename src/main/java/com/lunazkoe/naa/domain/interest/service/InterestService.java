package com.lunazkoe.naa.domain.interest.service;

import com.lunazkoe.naa.domain.interest.dto.request.InterestRegisterRequest;
import com.lunazkoe.naa.domain.interest.dto.response.InterestDto;
import com.lunazkoe.naa.domain.interest.entity.Interest;
import com.lunazkoe.naa.domain.interest.repository.InterestRepository;
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
        // TODO: 유사 관심사 중복 처리 - 현재는 일부러 중복 허요

        Interest newInterest = new Interest(request.name(), request.keywords());
        Interest savedInterest = interestRepository.save(newInterest);

        log.info("관심사 등록 완료. InterestId: {}", savedInterest.getId());
        return InterestDto.from(savedInterest, false);
        // - 관심사를 등록을 했을 경우 무조건 처음에는 구독이 되어있지 않으므로 false
    }
}
