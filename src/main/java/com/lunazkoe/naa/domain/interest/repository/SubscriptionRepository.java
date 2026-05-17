package com.lunazkoe.naa.domain.interest.repository;

import com.lunazkoe.naa.domain.interest.entity.Subscription;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query("SELECT s.interest.id FROM Subscription s WHERE s.user.id = :userId AND s.interest.id IN :interestIds")
    Set<UUID> findSubscribedInterestIdsByUserIdAndInterestIds(@Param("userId") UUID userId,
            @Param("interestIds") List<UUID> interestIds);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.interest WHERE s.interest.id = :interestId AND s.user.id = :userId")
    Optional<Subscription> findByInterestIdAndUserIdWithInterest(
            @Param("interestId") UUID interestId, @Param("userId") UUID userId);
}
