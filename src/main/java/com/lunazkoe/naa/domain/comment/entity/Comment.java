package com.lunazkoe.naa.domain.comment.entity;

import static com.lunazkoe.naa.global.entity.BaseSoftDeleteEntity.IS_DELETED_FALSE_ONLY;

import com.lunazkoe.naa.domain.article.entity.Article;
import com.lunazkoe.naa.domain.user.entity.User;
import com.lunazkoe.naa.global.entity.BaseSoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

// TODO: 조회 빈도에 따른 인덱스 생성

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(IS_DELETED_FALSE_ONLY)
public class Comment extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private long likeCount;

    public Comment(Article article, User user, String content) {
        this.article = article;
        this.user = user;
        this.content = content;
        this.likeCount = 0;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    // TODO: increase / decrease 동시성
    public void increaseCommentLikeCount() {
        if (this.likeCount < Long.MAX_VALUE) {
            this.likeCount++;
        }
    }

    public void decreaseCommentLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
