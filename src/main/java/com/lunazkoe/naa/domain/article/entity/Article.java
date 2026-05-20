package com.lunazkoe.naa.domain.article.entity;

import static com.lunazkoe.naa.global.entity.BaseSoftDeleteEntity.IS_DELETED_FALSE_ONLY;

import com.lunazkoe.naa.global.entity.BaseSoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

// TODO: 조회 빈도에 따른 인덱스 생성
// TODO: increase / decrease 동시성 문제
// TODO: sourceUrl, title, summary 등 가변 배열 등 DB의 어떤 걸 사용하는지

@Entity
@Table(
        name = "articles", indexes = {
        @Index(
                name = "uk_articles_source_url",
                columnList = "sourceUrl",
                unique = true
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(IS_DELETED_FALSE_ONLY)
public class Article extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Source source;

    @Column(nullable = false)
    private String sourceUrl;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime publishDate;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false)
    private long commentCount = 0L;

    @Column(nullable = false)
    private long viewCount = 0L;

    public Article(Source source, String sourceUrl, String title, LocalDateTime publishDate,
            String summary) {
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.publishDate = publishDate;
        this.summary = summary;
        this.commentCount = 0L;
        this.viewCount = 0L;
    }

    public void increaseCommentCount() {
        if (this.commentCount < Long.MAX_VALUE) {
            this.commentCount++;
        }
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void increaseViewCount() {
        if (this.viewCount < Long.MAX_VALUE) {
            this.viewCount++;
        }
    }
}
