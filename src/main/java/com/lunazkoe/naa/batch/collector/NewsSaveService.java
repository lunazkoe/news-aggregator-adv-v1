package com.lunazkoe.naa.batch.collector;

import com.lunazkoe.naa.batch.collector.provider.CollectedNewsDto;
import com.lunazkoe.naa.domain.article.entity.Article;
import com.lunazkoe.naa.domain.article.repository.ArticleRepository;
import com.lunazkoe.naa.domain.interest.repository.InterestRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: 근데 DB에 께속 데이터를 쌓을 순 없으니깐 백업과 동시에 삭제를 해야하지 않을까?

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsSaveService {

    private final InterestRepository interestRepository;
    private final ArticleRepository articleRepository;

    /**
     * 수집된 대량의 데이터를 가져와 닥 한 번만 DB를 조회해 중복을 제거한 뒤 일괄 저장함 외부 API 통신이 끝난 이후이므로 트랜잭션이 물려있지 않게 됨
     */
    @Transactional
    public int saveUniqueArticles(List<CollectedNewsDto> candidates) {
        if (candidates.isEmpty()) {
            return 0;
        }

        List<String> targetUrls = candidates.stream()
                .map(CollectedNewsDto::sourceUrl)
                .toList();

        // 500개 단위 쪼개어 조회 (PostgreSQL 인덱스 최적화 및 파라미터 한도 해제)
        Set<String> alreadySavedUrls = new HashSet<>();
        int batchSize = 500;
        for (int i = 0; i < targetUrls.size(); i += batchSize) {
            List<String> subList = targetUrls.subList(i,
                    Math.min(i + batchSize, targetUrls.size()));
            List<Article> existingArticles = articleRepository.findBySourceUrlIn(subList);
            for (Article article : existingArticles) {
                alreadySavedUrls.add(article.getSourceUrl());
            }
        }

        List<Article> articlesToSave = new ArrayList<>();
        for (CollectedNewsDto dto : candidates) {
            if (alreadySavedUrls.contains(dto.sourceUrl())) {
                continue;
            }

            Article article = new Article(
                    dto.source(),
                    dto.sourceUrl(),
                    dto.title(),
                    dto.publishDate(),
                    dto.summary()
            );
            articlesToSave.add(article);
        }

        if (!articlesToSave.isEmpty()) {
            articleRepository.saveAll(articlesToSave);
            return articlesToSave.size();
        }
        return 0;
    }
}
