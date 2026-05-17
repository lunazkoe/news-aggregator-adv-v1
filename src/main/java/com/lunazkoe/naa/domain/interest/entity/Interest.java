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
    private Integer subscriberCount = 0;

    public Interest(String name, List<String> keywords) {
        this.name = name;
        this.keywords = keywords != null ? keywords : new ArrayList<>();
        this.subscriberCount = 0;
    }

    public void updateKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

}
