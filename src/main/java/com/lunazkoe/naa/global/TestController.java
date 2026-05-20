package com.lunazkoe.naa.global;

import com.lunazkoe.naa.domain.article.entity.Article;
import com.lunazkoe.naa.domain.article.entity.Source;
import com.lunazkoe.naa.domain.article.repository.ArticleRepository;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test-api")
@RequiredArgsConstructor
public class TestController {

    private final ArticleRepository articleRepository;

    @Operation(summary = "Swagger Config Test", description = "Swagger Config 적용 테스트")
    @GetMapping
    public String swaggerConfigTest() {
        log.info("Swagger Config Test API Call");
        return "Swagger Config Test";
    }

    @GetMapping("/news-add")
    public void addNews() {
        Article article = new Article(
                Source.NAVER,
                "https://www.naver.com",
                "TestNews",
                LocalDateTime.now(),
                "testSummary"
        );
        articleRepository.save(article);
    }
}
