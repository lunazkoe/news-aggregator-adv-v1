package com.lunazkoe.naa.batch.collector.provider;

import com.lunazkoe.naa.domain.article.entity.Source;
import java.time.LocalDateTime;
import java.util.UUID;

public record CollectedNewsDto(
        Source source,
        String sourceUrl,
        String title,
        LocalDateTime publishDate,
        String summary,
        UUID interestId
) {

}
