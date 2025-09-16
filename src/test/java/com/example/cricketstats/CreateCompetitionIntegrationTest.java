package com.example.cricketstats;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateCompetitionIntegrationTest {

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.1.0")
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

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldSaveItemToDatabase() throws Exception {
        // // 1. Call the REST API (this will insert into DB)
        // var request = new ItemDto("Some Name", 123);
        // var response = restTemplate.postForEntity(
        //         "http://localhost:" + port + "/items",
        //         request,
        //         Void.class
        // );
        // assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        // // 2. Directly query the Testcontainers MySQL
        // try (Connection conn = mysql.createConnection("");
        //      Statement stmt = conn.createStatement();
        //      ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM items WHERE name='Some Name'")) {

        //     rs.next();
        //     int count = rs.getInt(1);
        //     assertThat(count).isEqualTo(1);
        // }
    }
}

