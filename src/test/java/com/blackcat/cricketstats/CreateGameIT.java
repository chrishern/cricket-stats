package com.blackcat.cricketstats;

import com.blackcat.cricketstats.application.dto.CreateGameRequest;
import com.blackcat.cricketstats.application.dto.ErrorResponse;
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
public class CreateGameIT {

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

    private void setupTestData() throws Exception {
        try (Connection conn = mysql.createConnection("");
             Statement stmt = conn.createStatement()) {

            stmt.execute("INSERT INTO competition (id, format, start_year, end_year, country, international, name) " +
                        "VALUES (1, 'FIRST_CLASS', '2023', '2024', 'ENGLAND', false, 'County Championship')");
        }
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
    public void shouldReturnBadRequestWhenScorecardUrlIsBlank() throws Exception {
        var request = new CreateGameRequest();
        request.setScorecardUrl("");

        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/games",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Scorecard URL is required");
    }

    @Test
    public void shouldReturnBadRequestWhenScorecardUrlIsNull() throws Exception {
        var request = new CreateGameRequest();
        request.setScorecardUrl(null);

        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/games",
                request,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Scorecard URL is required");
    }

    @Test
    public void shouldCreateGameSuccessfully() throws Exception {
        setupTestData();

        var request = new CreateGameRequest();
        request.setScorecardUrl("https://www.bbc.co.uk/sport/cricket/scorecard/e-225742");

        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/games",
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNull();
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).matches("/api/games/\\d+");

        try (Connection conn = mysql.createConnection("");
             Statement stmt = conn.createStatement()) {

            ResultSet gameRs = stmt.executeQuery("SELECT * FROM game WHERE id = 1");
            assertThat(gameRs.next()).isTrue();
            assertThat(gameRs.getInt("competition")).isEqualTo(1);
            assertThat(gameRs.getString("result")).isEqualTo("Match Drawn");

            int homeTeamId = gameRs.getInt("home_team");
            int awayTeamId = gameRs.getInt("away_team");
            gameRs.close();

            ResultSet homeTeamRs = stmt.executeQuery("SELECT * FROM team WHERE id = " + homeTeamId);
            assertThat(homeTeamRs.next()).isTrue();
            assertThat(homeTeamRs.getString("name")).isEqualTo("Durham");
            assertThat(homeTeamRs.getString("country")).isEqualTo("England");
            assertThat(homeTeamRs.getBoolean("international")).isFalse();
            homeTeamRs.close();

            ResultSet awayTeamRs = stmt.executeQuery("SELECT * FROM team WHERE id = " + awayTeamId);
            assertThat(awayTeamRs.next()).isTrue();
            assertThat(awayTeamRs.getString("name")).isEqualTo("Worcestershire");
            assertThat(awayTeamRs.getString("country")).isEqualTo("England");
            assertThat(awayTeamRs.getBoolean("international")).isFalse();
            awayTeamRs.close();
        }
    }
}