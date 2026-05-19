package com.lunazkoe.naa.domain.comment.repository;

import com.lunazkoe.naa.domain.comment.entity.CommentLike;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

    @Query("SELECT cl FROM CommentLike cl " +
            "JOIN FETCH cl.comment " +
            "JOIN FETCH cl.user " +
            "WHERE cl.comment.id = :commentId AND cl.user.id = :userId")
    Optional<CommentLike> findByCommentIdAndUserIdWithFetch(
            @Param("commentId") UUID commentId,
            @Param("userId") UUID userId
    );

    Optional<CommentLike> findByUserIdAndCommentId(UUID userId, UUID commentId);

    @Query("SELECT cl FROM CommentLike cl WHERE cl.user.id = :userId AND cl.comment.id = :commentId")
    Optional<CommentLike> findByUserIdAndCommentIdWithoutJoin(
            @Param("userId") UUID userId,
            @Param("commentId") UUID commentId
    );
}
