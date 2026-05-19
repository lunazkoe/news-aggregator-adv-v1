package com.lunazkoe.naa.domain.comment.repository;

import com.lunazkoe.naa.domain.comment.entity.Comment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // User만 함께 가져오기 - CommentDto.from()용
    // - getArticle().getId()를 호출할 때는 추가 쿼리가 나가지 않음 (외래키로 이미 들고 있기 때문에) - getArticle().getTitle() 같이 Article의 데이터에 접근할 때 추가 쿼리가 발생하는 것
    // - 때문에 User는 함께 가져와야함
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.id = :commentId")
    Optional<Comment> findByIdWithUser(@Param("commentId") UUID commentId);
}
