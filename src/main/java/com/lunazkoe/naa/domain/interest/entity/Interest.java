package com.lunazkoe.naa.domain.interest.entity;

import com.lunazkoe.naa.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// TODO: 조회 빈도에 따른 인덱스
@Entity
@Table(name = "interests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "varchar[]", nullable = false)
    private List<String> keywords = new ArrayList<>();

    @Column(name = "subscriber_count", nullable = false)
    private Long subscriberCount = 0L;

    public Interest(String name, List<String> keywords) {
        this.name = name;
        this.keywords = keywords != null ? keywords : new ArrayList<>();
        this.subscriberCount = 0L;
    }

    // TODO: 키워드 업데이트 로직을 어떻게 하는 것이 좋을지 알아보고 선택하기
    public void updateKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void increaseSubscriberCount() {
        if (this.subscriberCount < Long.MAX_VALUE) {
            this.subscriberCount++;
        }
    }

    public void decreaseSubscriberCount() {
        if (this.subscriberCount > 0) {
            this.subscriberCount--;
        }
    }
}
