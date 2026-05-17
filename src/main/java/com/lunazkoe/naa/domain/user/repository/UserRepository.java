package com.lunazkoe.naa.domain.user.repository;

import com.lunazkoe.naa.domain.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);
}