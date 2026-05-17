package com.lunazkoe.naa.domain.interest.repository;

import com.lunazkoe.naa.domain.interest.dto.request.InterestSearchCondition;
import com.lunazkoe.naa.domain.interest.entity.Interest;
import com.lunazkoe.naa.global.dto.CursorPageResponse;

public interface InterestRepositoryCustom {

    CursorPageResponse<Interest> searchInterests(InterestSearchCondition condition);
}
