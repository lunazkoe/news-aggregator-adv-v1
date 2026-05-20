package com.lunazkoe.naa.batch.collector.provider;

import com.lunazkoe.naa.domain.article.entity.Source;
import com.lunazkoe.naa.domain.interest.entity.Interest;
import java.util.List;

public interface NewsProvider {

    // 특정 관심사의 키워드들을 기반으로 뉴스를 수집
    List<CollectedNewsDto> fetchNews(Interest interest);

    // 어떤 Source(Naver, RSS)를 사용하는지 반환
    Source getSource();
}
