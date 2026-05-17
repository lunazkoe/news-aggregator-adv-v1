package com.lunazkoe.naa.domain.user.entity;

import static com.lunazkoe.naa.global.entity.BaseSoftDeleteEntity.IS_DELETED_FALSE_ONLY;

import com.lunazkoe.naa.global.entity.BaseSoftDeleteEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(IS_DELETED_FALSE_ONLY)
public class User extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
