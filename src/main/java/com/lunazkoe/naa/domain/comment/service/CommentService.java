package com.lunazkoe.naa.domain.comment.service;

import com.lunazkoe.naa.domain.article.entity.Article;
import com.lunazkoe.naa.domain.article.exception.ArticleErrorCode;
import com.lunazkoe.naa.domain.article.exception.ArticleException;
import com.lunazkoe.naa.domain.article.repository.ArticleRepository;
import com.lunazkoe.naa.domain.comment.dto.request.CommentRegisterRequest;
import com.lunazkoe.naa.domain.comment.dto.request.CommentUpdateRequest;
import com.lunazkoe.naa.domain.comment.dto.response.CommentDto;
import com.lunazkoe.naa.domain.comment.entity.Comment;
import com.lunazkoe.naa.domain.comment.exception.CommentErrorCode;
import com.lunazkoe.naa.domain.comment.exception.CommentException;
import com.lunazkoe.naa.domain.comment.repository.CommentRepository;
import com.lunazkoe.naa.domain.user.entity.User;
import com.lunazkoe.naa.domain.user.exception.UserErrorCode;
import com.lunazkoe.naa.domain.user.exception.UserException;
import com.lunazkoe.naa.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: 동시성 문제
// TODO: 권한 인증 문제

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 목로 조회
     */

    /**
     * 댓글 등록
     */
    @Transactional
    public CommentDto createComment(CommentRegisterRequest request) {
        Article foundArticle = getFoundArticleById(request);
        User foundUser = getFoundUserById(request);

        Comment newComment = new Comment(foundArticle, foundUser, request.content());
        Comment savedComment = commentRepository.save(newComment);

        // 기사의 댓글 수 증가
        foundArticle.increaseCommentCount();

        log.info("댓글 등록 완료. CommentId: {}", savedComment.getId());
        return CommentDto.from(savedComment, false);
        // - TODO: 댓글 좋아요 기능 미구현 -> 그래도 최초 생성 시 본인에 의한 좋아요는 false
    }

    /**
     * 관심사 댓글 좋아요
     */

    /**
     * 댓글 좋아요 취소
     */

    /**
     * 댓글 논리 삭제
     */
    @Transactional
    public void softDelete(UUID commentId) {
        Comment foundComment = getFoundCommentById(commentId);

        foundComment.softDelete();

        // TODO: 연관관게에 따른 논리 삭제 구현 (아직 연관관계 및 논리 삭제에 대한 행동 정의가 부족함)
        // -  기사 댓글 수 감소같은거 추가
        log.info("댓글 논리 삭제 완료. CommentId: {}", foundComment.getId());
    }

    /**
     * 댓글 정보 수정
     */
    @Transactional
    public CommentDto updateCommentContent(UUID commentId,
            UUID requestUserId, CommentUpdateRequest request) {
        Comment foundComment = getFoundCommentByIdWithUser(commentId);

        foundComment.updateContent(request.content());

        log.info("댓글 정보 수정 완료. CommentId: {}", foundComment.getId());
        return CommentDto.from(foundComment, false);
        // TODO: 좋아요 관련 로직 작성 후 requestUserId + commentId 기반으로 likedByMe 찾아서 추가
    }

    /**
     * 댓글 물리 삭제
     */
    @Transactional
    public void hardDelete(UUID commentId) {
        Comment foundComment = getFoundCommentById(commentId);

        // TODO: 연관관계에 따른 물리 삭제 구현 예정

        commentRepository.delete(foundComment);
        log.info("댓글 물리 삭제 완료. CommentId: {}", foundComment.getId());
    }

    private Comment getFoundCommentById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
    }

    private Comment getFoundCommentByIdWithUser(UUID commentId) {
        return commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
    }

    private User getFoundUserById(CommentRegisterRequest request) {
        return userRepository.findById(request.userId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Article getFoundArticleById(CommentRegisterRequest request) {
        return articleRepository.findById(request.articleId())
                .orElseThrow(() -> new ArticleException(ArticleErrorCode.ARTICLE_NOT_FOUND));
    }
}
