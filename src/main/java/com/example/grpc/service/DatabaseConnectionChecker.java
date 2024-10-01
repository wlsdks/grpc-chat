package com.example.grpc.service;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * 스프링 부트 애플리케이션이 시작된 후 데이터베이스 연결을 확인하는 클래스
 */
@Slf4j
@Component
public class DatabaseConnectionChecker implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public DatabaseConnectionChecker(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        checkDatabaseConnection();
        checkDataSourceStatus();
    }

    /**
     * @apiNote 데이터베이스 연결 확인
     */
    private void checkDatabaseConnection() {
        try {
            transactionTemplate.execute(status -> {
                Object selectCurrentTimestamp = entityManager.createNativeQuery("SELECT CURRENT_TIMESTAMP").getSingleResult();
                log.info("Current timestamp: {}", selectCurrentTimestamp);
                log.info("Database connection is OK!");
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to connect to the database", e);
        }
    }

    /**
     * @apiNote 데이터 소스 상태 확인
     */
    private void checkDataSourceStatus() {
        try {
            DataSource dataSource = jdbcTemplate.getDataSource();

            if (dataSource != null) {
                log.info("DataSource class: {}", dataSource.getClass().getName());

//                // Unwrap DecoratedDataSource if present
//                if (dataSource instanceof DecoratedDataSource decoratedDataSource) {
//                    log.info("Unwrapped DataSource class: {}", decoratedDataSource.getClass().getName());
//                }

                if (dataSource instanceof HikariDataSource hikariDataSource) {
                    log.info("HikariCP connection pool status:");
                    log.info("  Max pool size: {}", hikariDataSource.getMaximumPoolSize());
                    log.info("  Active connections: {}", hikariDataSource.getHikariPoolMXBean().getActiveConnections());
                    log.info("  Idle connections: {}", hikariDataSource.getHikariPoolMXBean().getIdleConnections());
                }
            } else {
                log.warn("DataSource not found!");
            }
        } catch (Exception e) {
            log.error("Error checking DataSource status", e);
        }
    }

}