package com.blackcat.cricketstats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {

    // Singleton container shared across all integration tests
    static final MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:9.1.0")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withEnv("MYSQL_ROOT_PASSWORD", "test")
                    .withStartupTimeout(Duration.ofMinutes(10));

    static {
        mysql.start();
    }

    @DynamicPropertySource
    static void configureProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @BeforeEach
    public void cleanDatabase() throws Exception {
        try (Connection conn = mysql.createConnection("");
            Statement stmt = conn.createStatement()) {

            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            // 1. Collect table names first
            List<String> tables = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
            }

            // 2. Now truncate
            for (String table : tables) {
                stmt.execute("TRUNCATE TABLE " + table);
            }

            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    
}