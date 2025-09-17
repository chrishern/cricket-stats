package com.blackcat.cricketstats;

import com.blackcat.cricketstats.application.dto.CreateCompetitionRequest;
import com.blackcat.cricketstats.application.dto.ErrorResponse;
import com.blackcat.cricketstats.domain.competition.Format;
import com.blackcat.cricketstats.domain.competition.Country;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class CreateCompetitionIntegrationTest {

    private static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.1.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withEnv("MYSQL_ROOT_PASSWORD", "test")
            .withStartupTimeout(Duration.ofMinutes(10));

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    private static void setup() {
        mysql.start();
    }

    @DynamicPropertySource
    private static void configureProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @AfterAll
    private static void teardown() {
        mysql.stop();
    }

    @Test
    public void shouldCreateCompetitionSuccessfully() throws Exception {
        // Given
        var request = new CreateCompetitionRequest();
        
        request.setFormat(Format.T_20);
        request.setStartYear("2023");
        request.setEndYear("2024");
        request.setCountry(Country.ENGLAND);
        request.setInternational(true);
        request.setName("Test Competition");

        // When
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/competitions",
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNull();
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).matches("/api/competitions/\\d+");

        try (Connection conn = mysql.createConnection("");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM competition WHERE name='Test Competition'")) {

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Test Competition");
            assertThat(rs.getString("format")).isEqualTo("T_20");
            assertThat(rs.getString("start_year")).isEqualTo("2023");
            assertThat(rs.getString("end_year")).isEqualTo("2024");
            assertThat(rs.getString("country")).isEqualTo("ENGLAND");
            assertThat(rs.getBoolean("international")).isTrue();
        }
    }

    @Test
    public void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        // Given
        var request = new CreateCompetitionRequest();
        request.setFormat(Format.T_20);
        request.setStartYear("2023");
        request.setEndYear("2024");
        request.setCountry(Country.ENGLAND);
        request.setInternational(true);
        request.setName("");

        // When
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/competitions",
                request,
                ErrorResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Name is required");
    }

    @Test
    public void shouldReturnBadRequestWhenNameIsNull() throws Exception {
        // Given
        var request = new CreateCompetitionRequest();
        request.setFormat(Format.T_20);
        request.setStartYear("2023");
        request.setEndYear("2024");
        request.setCountry(Country.ENGLAND);
        request.setInternational(true);
        request.setName(null);

        // When
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/competitions",
                request,
                ErrorResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Name is required");
    }
}