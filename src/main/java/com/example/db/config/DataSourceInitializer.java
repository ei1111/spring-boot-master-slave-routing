package com.example.db.config;


import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
@Component
public class DataSourceInitializer implements CommandLineRunner {

    private final ApplicationContext context;

    public DataSourceInitializer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("=== DataSource Initialization Check ===");

        // 빈 이름으로 직접 가져오기
        DataSource masterDataSource = (DataSource) context.getBean("masterDataSource");
        DataSource slaveDataSource = (DataSource) context.getBean("slaveDataSource");

        // Master 확인
        if (masterDataSource instanceof HikariDataSource) {
            HikariDataSource hikari = (HikariDataSource) masterDataSource;
            log.info("✅ Master DataSource");
            log.info("   - Pool Name: {}", hikari.getPoolName());
            log.info("   - JDBC URL: {}", hikari.getJdbcUrl());

            try (Connection conn = hikari.getConnection()) {
                log.info("   - Connected URL: {}", conn.getMetaData().getURL());
            }
        }

        // Slave 확인
        if (slaveDataSource instanceof HikariDataSource) {
            HikariDataSource hikari = (HikariDataSource) slaveDataSource;
            log.info("✅ Slave DataSource");
            log.info("   - Pool Name: {}", hikari.getPoolName());
            log.info("   - JDBC URL: {}", hikari.getJdbcUrl());

            try (Connection conn = hikari.getConnection()) {
                log.info("   - Connected URL: {}", conn.getMetaData().getURL());
            }
        }

        log.info("=== DataSource Initialization Complete ===");
    }
}
