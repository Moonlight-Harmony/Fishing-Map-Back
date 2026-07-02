package com.moonlightharmony.fishingmapback.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {
    @PersistenceContext
    EntityManager em;

    @Bean
    public JPAQueryFactory queryFactory(){
        return new JPAQueryFactory(em);
    }
}
