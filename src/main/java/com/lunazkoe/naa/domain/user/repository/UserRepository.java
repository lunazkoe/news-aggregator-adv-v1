package com.lunazkoe.naa.domain.user.repository;

import com.lunazkoe.naa.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    // 해당 이메일로 가입된 사용자 유무
    // - 논리 삭제된 사용자는 조회되지 않음
    boolean existsByEmail(String email);

    // 해당 이메일로 가입된 사용자 조회
    Optional<User> findByEmail(String email);
}