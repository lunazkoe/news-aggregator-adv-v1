package com.lunazkoe.naa.domain.article.repository;

import com.lunazkoe.naa.domain.article.entity.Article;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleRepositoryCustom {

}
