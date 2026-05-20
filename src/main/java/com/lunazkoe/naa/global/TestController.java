package com.lunazkoe.naa.global;

import com.lunazkoe.naa.batch.collector.NewsCollectorService;
import com.lunazkoe.naa.domain.article.repository.ArticleRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final ArticleRepository articleRepository;
    private final NewsCollectorService newsCollectorService;

    @Operation(summary = "Swagger Config Test", description = "Swagger Config 적용 테스트")
    @GetMapping
    public String swaggerConfigTest() {
        log.info("Swagger Config Test API Call");
        return "Swagger Config Test";
    }

    @Operation(summary = "수동 뉴스 배치 작업", description = "수동으로 뉴스 배치 작업을 돌립니다.")
    @GetMapping("/news-batch")
    public void newsBatch() {
        newsCollectorService.collectNewsHourly();
    }
}
