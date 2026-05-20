package com.lunazkoe.naa.domain.notification.entity;

import com.lunazkoe.naa.domain.user.entity.User;
import com.lunazkoe.naa.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_user_confirmed", columnList = "user_id, confirmed")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private boolean confirmed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    public Notification(User user, String content, ResourceType resourceType, UUID resourceId) {
        this.confirmed = false;
        this.user = user;
        this.content = content;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public void confirm() {
        if (!this.confirmed) {
            this.confirmed = true;
        }
    }
}
