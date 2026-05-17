package com.lunazkoe.naa.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

    @PersistenceContext
    // - EntityManagerFactor와 달리 Thread-Safe하지 않기 때문에 동시성 문제가 발생할 수 있음
    // - Spring에서 EntityManager를 Proxy로 감싼 entityManager를 생성해서 주입하여 Thread-Safe를 보장함
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
