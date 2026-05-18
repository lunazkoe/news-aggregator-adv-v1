package com.lunazkoe.naa.domain.article.repository;

import com.lunazkoe.naa.domain.article.dto.request.ArticleSearchCondition;
import com.lunazkoe.naa.domain.article.entity.Article;
import com.lunazkoe.naa.global.dto.CursorPageResponse;

public interface ArticleRepositoryCustom {

    CursorPageResponse<Article> searchArticles(ArticleSearchCondition condition);
}
