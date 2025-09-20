package com.blackcat.cricketstats;

import com.blackcat.cricketstats.application.dto.CreateGameRequest;
import com.blackcat.cricketstats.application.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateGameIT extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private void setupExistingCompetition() throws Exception {
        try (Connection conn = mysql.createConnection("");
             Statement stmt = conn.createStatement()) {

            stmt.execute("INSERT INTO competition (id, format, start_year, end_year, country, international, name) " +
                        "VALUES (1, 'T_20', '2024', '2024', 'ENGLAND', false, 'Rothesay County Championship Division 1')");
        }
    }

    @Test
    public void shouldReturnBadRequestWhenScorecardUrlIsBlank() throws Exception {
        // Given
        var request = new CreateGameRequest();
        request.setScorecardUrl("");

        // When
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/games",
                request,
                ErrorResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Scorecard URL is required");
    }

    @Test
    public void shouldReturnBadRequestWhenScorecardUrlIsNull() throws Exception {
        // Given
        var request = new CreateGameRequest();
        request.setScorecardUrl(null);

        // When
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/games",
                request,
                ErrorResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Scorecard URL is required");
    }

    @Test
    public void shouldCreateGameSuccessfully() throws Exception {
        // Given - no setup needed, competition will be auto-created
        var request = new CreateGameRequest();
        request.setScorecardUrl("https://www.bbc.co.uk/sport/cricket/scorecard/e-225742");

        // When
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/games",
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNull();
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).matches("/api/games/\\d+");

        try (Connection conn = mysql.createConnection("");
             Statement stmt = conn.createStatement()) {

            ResultSet gameRs = stmt.executeQuery("SELECT * FROM game WHERE id = 1");
            assertThat(gameRs.next()).isTrue();

            int competitionId = gameRs.getInt("competition");
            assertThat(gameRs.getString("result")).isEqualTo("Match Drawn");
            assertThat(gameRs.getTimestamp("start_date_time")).isNotNull();
            assertThat(gameRs.getTimestamp("start_date_time").toString()).isEqualTo("2025-09-15 09:30:00.0");

            int homeTeamId = gameRs.getInt("home_team");
            int awayTeamId = gameRs.getInt("away_team");
            gameRs.close();

            // Verify the competition was created correctly
            ResultSet competitionRs = stmt.executeQuery("SELECT * FROM competition WHERE id = " + competitionId);
            assertThat(competitionRs.next()).isTrue();
            assertThat(competitionRs.getString("name")).isEqualTo("Rothesay County Championship Division 1");
            assertThat(competitionRs.getString("format")).isEqualTo("T_20");
            assertThat(competitionRs.getString("country")).isEqualTo("ENGLAND");
            assertThat(competitionRs.getBoolean("international")).isFalse();
            competitionRs.close();

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

    @Test
    public void shouldCreateGameWithExistingCompetition() throws Exception {
        // Given - set up existing competition
        setupExistingCompetition();

        var request = new CreateGameRequest();
        request.setScorecardUrl("https://www.bbc.co.uk/sport/cricket/scorecard/e-225742");

        // When
        var response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/games",
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNull();
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).matches("/api/games/\\d+");

        try (Connection conn = mysql.createConnection("");
             Statement stmt = conn.createStatement()) {

            ResultSet gameRs = stmt.executeQuery("SELECT * FROM game WHERE id = 1");
            assertThat(gameRs.next()).isTrue();

            // Should use the existing competition (ID 1)
            assertThat(gameRs.getInt("competition")).isEqualTo(1);
            assertThat(gameRs.getString("result")).isEqualTo("Match Drawn");
            assertThat(gameRs.getTimestamp("start_date_time")).isNotNull();
            assertThat(gameRs.getTimestamp("start_date_time").toString()).isEqualTo("2025-09-15 09:30:00.0");

            int homeTeamId = gameRs.getInt("home_team");
            int awayTeamId = gameRs.getInt("away_team");
            gameRs.close();

            // Verify only one competition exists (the one we created, not a new one)
            ResultSet competitionCountRs = stmt.executeQuery("SELECT COUNT(*) as count FROM competition");
            assertThat(competitionCountRs.next()).isTrue();
            assertThat(competitionCountRs.getInt("count")).isEqualTo(1);
            competitionCountRs.close();

            // Verify it's the correct competition
            ResultSet competitionRs = stmt.executeQuery("SELECT * FROM competition WHERE id = 1");
            assertThat(competitionRs.next()).isTrue();
            assertThat(competitionRs.getString("name")).isEqualTo("Rothesay County Championship Division 1");
            competitionRs.close();

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