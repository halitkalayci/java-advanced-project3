package com.smartorder.orderservice.infrastructure.scheduler;

import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Enables ShedLock so that, when more than one order-service instance runs, only
 * one of them publishes the outbox at a time — preventing duplicate Kafka events.
 * The lock is stored in the {@code shedlock} table (see Flyway V2).
 */
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
class SchedulerLockConfig {

    @Bean
    LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .build());
    }
}
