package com.lunazkoe.naa.domain.interest.dto.response;

import com.lunazkoe.naa.domain.interest.entity.Interest;
import java.util.List;
import java.util.UUID;

public record InterestDto(
        UUID id,
        String name,
        List<String> keywords,
        Long subscriberCount,
        Boolean subscribedByMe
) {

    public static InterestDto from(Interest interest, boolean subscribedByMe) {
        return new InterestDto(
                interest.getId(),
                interest.getName(),
                interest.getKeywords(),
                interest.getSubscriberCount(),
                subscribedByMe
        );
    }
}
