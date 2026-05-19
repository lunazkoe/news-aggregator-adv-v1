package com.lunazkoe.naa;

import com.lunazkoe.naa.domain.article.entity.Article;
import com.lunazkoe.naa.domain.article.entity.Source;
import com.lunazkoe.naa.domain.article.repository.ArticleRepository;
import com.lunazkoe.naa.domain.comment.entity.Comment;
import com.lunazkoe.naa.domain.comment.entity.CommentLike;
import com.lunazkoe.naa.domain.comment.repository.CommentLikeRepository;
import com.lunazkoe.naa.domain.comment.repository.CommentRepository;
import com.lunazkoe.naa.domain.comment.service.CommentService;
import com.lunazkoe.naa.domain.user.entity.User;
import com.lunazkoe.naa.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
public class TestJPA {

    @Autowired
    CommentService commentService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    void test() {
        Article article = new Article(
                Source.NAVER,
                "testUrl",
                "testTitle",
                LocalDateTime.now(),
                "testSummary"
        );

        User user = new User(
                "test@gmail.com",
                "testNickname",
                "testPassword"
        );

        Article savedArticle = articleRepository.save(article);
        User savedUser = userRepository.save(user);

        Comment comment = new Comment(savedArticle, savedUser, "testContent");
        Comment savedComment = commentRepository.save(comment);

        em.flush();
        em.clear();

//        log.info("commentService.updateCommentContent()");
//        commentService.updateCommentContent(savedComment.getId(), savedUser.getId(),
//                new CommentUpdateRequest("updateContent"));

//        Optional<Comment> commetOptional = commentRepository.findById(savedComment.getId());
//        Comment foundComment = commetOptional.get();
//        log.info("foundComment.getArticle().getId()");
//        log.info("foundComment.getAritlce().getId(): {}", foundComment.getArticle().getId());
//
//        log.info("foundComment.getArticle().getTitle()");
//        log.info("foundComment.getArticle().getTitle(): {}", foundComment.getArticle().getTitle());

        CommentLike commentLike = new CommentLike(savedComment, savedUser);

        em.flush();
        em.clear();

        log.info("userid and comment id 쿼리 확인해보기");
        Optional<CommentLike> ppp = commentLikeRepository.findByUserIdAndCommentId(
                savedUser.getId(), savedComment.getId());
    }
}
