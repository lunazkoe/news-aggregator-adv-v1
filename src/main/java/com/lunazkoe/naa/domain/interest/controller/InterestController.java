package com.lunazkoe.naa.domain.interest.controller;

import com.lunazkoe.naa.domain.interest.dto.request.InterestRegisterRequest;
import com.lunazkoe.naa.domain.interest.dto.request.InterestUpdateRequest;
import com.lunazkoe.naa.domain.interest.dto.response.InterestDto;
import com.lunazkoe.naa.domain.interest.service.InterestService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interests")
public class InterestController {

    private final InterestService interestService;

    @Operation(summary = "관심사 등록", description = "새로운 관심사를 등록합니다.")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public InterestDto createInterest(@Valid @RequestBody InterestRegisterRequest request) {
        return interestService.createInterest(request);
    }

    @Operation(summary = "관심사 물리 삭제", description = "관심사를 물리적으로 삭제합니다.")
    @DeleteMapping("/{interestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDeleteInterest(@PathVariable UUID interestId) {
        interestService.hardDelete(interestId);
    }

    @Operation(summary = "관심사 정보 수정", description = "관심사의 키워드를 수정합니다.")
    @PatchMapping("/{interestId}")
    @ResponseStatus(HttpStatus.OK)
    public InterestDto updateInterestKeywords(@PathVariable UUID interestId, @Valid @RequestBody
    InterestUpdateRequest request) {
        return interestService.updateInterestKeywords(interestId, request);
    }
}
