package com.lunazkoe.naa.global;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test-api")
public class TestController {

    @Operation(summary = "Swagger Config Test", description = "Swagger Config 적용 테스트")
    @GetMapping
    public String swaggerConfigTest() {
        log.info("Swagger Config Test API Call");
        return "Swagger Config Test";
    }
}
