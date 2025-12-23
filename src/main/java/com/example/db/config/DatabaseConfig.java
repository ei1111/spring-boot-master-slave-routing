package com.example.db.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import com.zaxxer.hikari.HikariConfig;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.example")
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.master.hikari")
    public HikariConfig masterHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.slave.hikari")
    public HikariConfig slaveHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "masterDataSource")
    public DataSource masterDataSource(
            @Value("${spring.datasource.master.jdbc-url}") String jdbcUrl,
            @Value("${spring.datasource.master.username}") String username,
            @Value("${spring.datasource.master.password}") String password
    ) {
        HikariConfig config = masterHikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        log.info("Creating Master DataSource with pool: {}", config.getPoolName());
        return new HikariDataSource(config);
    }

    @Bean(name = "slaveDataSource")
    public DataSource slaveDataSource(
            @Value("${spring.datasource.slave.jdbc-url}") String jdbcUrl,
            @Value("${spring.datasource.slave.username}") String username,
            @Value("${spring.datasource.slave.password}") String password
    ) {
        HikariConfig config = slaveHikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        log.info("Creating Slave DataSource with pool: {}", config.getPoolName());
        return new HikariDataSource(config);
    }

    @Bean(name = "routingDataSource")
    public DataSource routingDataSource(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            @Qualifier("slaveDataSource") DataSource slaveDataSource) {

        log.info("Creating Routing DataSource");

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER, masterDataSource);
        targetDataSources.put(DataSourceType.SLAVE, slaveDataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource(
            @Qualifier("routingDataSource") DataSource routingDataSource) {
        log.info("Creating Lazy DataSource Proxy");
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em =
                new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(false);
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}