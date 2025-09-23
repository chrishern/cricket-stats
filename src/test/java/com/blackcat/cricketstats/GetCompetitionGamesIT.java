package com.blackcat.cricketstats;

import com.blackcat.cricketstats.application.dto.GameResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetCompetitionGamesIT extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldGetGamesByCompetitionIdSuccessfully() throws Exception {
        // Given - insert test data directly into database
        try (Connection conn = mysql.createConnection("");
             Statement stmt = conn.createStatement()) {

            // Insert competition
            stmt.execute("INSERT INTO competition (id, format, start_year, end_year, country, international, name) " +
                        "VALUES (1, 'T_20', '2023', '2024', 'ENGLAND', true, 'Test Competition')");

            // Insert teams
            stmt.execute("INSERT INTO team (id, country, international, name) " +
                        "VALUES (1, 'England', false, 'Team A')");
            stmt.execute("INSERT INTO team (id, country, international, name) " +
                        "VALUES (2, 'England', false, 'Team B')");
            stmt.execute("INSERT INTO team (id, country, international, name) " +
                        "VALUES (3, 'England', false, 'Team C')");

            // Insert games with different start_date_time to test ordering
            stmt.execute("INSERT INTO game (id, competition, home_team, away_team, result, start_date_time) " +
                        "VALUES (1, 1, 1, 2, 'Team A won by 5 wickets', '2023-06-15 14:00:00')");

            stmt.execute("INSERT INTO game (id, competition, home_team, away_team, result, start_date_time) " +
                        "VALUES (2, 1, 3, 1, 'Team C won by 10 runs', '2023-06-10 11:00:00')");

            // Game with same date as first game but different home team (for alphabetical ordering test)
            stmt.execute("INSERT INTO game (id, competition, home_team, away_team, result, start_date_time) " +
                        "VALUES (3, 1, 2, 3, 'Team B won by 2 wickets', '2023-06-15 14:00:00')");

            // Game with null start_date_time (should come last)
            stmt.execute("INSERT INTO game (id, competition, home_team, away_team, result, start_date_time) " +
                        "VALUES (4, 1, 1, 3, 'Team A won by 15 runs', null)");
        }

        // When
        var response = restTemplate.exchange(
                "http://localhost:" + port + "/api/competitions/1/games",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GameResponse>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(4);

        List<GameResponse> games = response.getBody();

        // Verify games are ordered by start_date_time (earliest first), then by home team name (alphabetical)
        // First: 2023-06-10 - Team C vs Team A
        assertThat(games.get(0).getId()).isEqualTo(2);
        assertThat(games.get(0).getHomeTeamId()).isEqualTo(3);
        assertThat(games.get(0).getHomeTeamName()).isEqualTo("Team C");
        assertThat(games.get(0).getAwayTeamId()).isEqualTo(1);
        assertThat(games.get(0).getAwayTeamName()).isEqualTo("Team A");
        assertThat(games.get(0).getResult()).isEqualTo("Team C won by 10 runs");
        assertThat(games.get(0).getStartDateTime()).isEqualTo("2023-06-10T11:00:00");

        // Second: 2023-06-15 - Team A vs Team B (alphabetically first by home team)
        assertThat(games.get(1).getId()).isEqualTo(1);
        assertThat(games.get(1).getHomeTeamId()).isEqualTo(1);
        assertThat(games.get(1).getHomeTeamName()).isEqualTo("Team A");
        assertThat(games.get(1).getAwayTeamId()).isEqualTo(2);
        assertThat(games.get(1).getAwayTeamName()).isEqualTo("Team B");
        assertThat(games.get(1).getResult()).isEqualTo("Team A won by 5 wickets");
        assertThat(games.get(1).getStartDateTime()).isEqualTo("2023-06-15T14:00:00");

        // Third: 2023-06-15 - Team B vs Team C (alphabetically second by home team)
        assertThat(games.get(2).getId()).isEqualTo(3);
        assertThat(games.get(2).getHomeTeamId()).isEqualTo(2);
        assertThat(games.get(2).getHomeTeamName()).isEqualTo("Team B");
        assertThat(games.get(2).getAwayTeamId()).isEqualTo(3);
        assertThat(games.get(2).getAwayTeamName()).isEqualTo("Team C");
        assertThat(games.get(2).getResult()).isEqualTo("Team B won by 2 wickets");
        assertThat(games.get(2).getStartDateTime()).isEqualTo("2023-06-15T14:00:00");

        // Fourth: null start_date_time - Team A vs Team C (should come last)
        assertThat(games.get(3).getId()).isEqualTo(4);
        assertThat(games.get(3).getHomeTeamId()).isEqualTo(1);
        assertThat(games.get(3).getHomeTeamName()).isEqualTo("Team A");
        assertThat(games.get(3).getAwayTeamId()).isEqualTo(3);
        assertThat(games.get(3).getAwayTeamName()).isEqualTo("Team C");
        assertThat(games.get(3).getResult()).isEqualTo("Team A won by 15 runs");
        assertThat(games.get(3).getStartDateTime()).isNull();
    }

    @Test
    public void shouldReturnEmptyListWhenNoGamesExistForCompetition() throws Exception {
        // Given - insert competition but no games
        try (Connection conn = mysql.createConnection("");
             Statement stmt = conn.createStatement()) {

            stmt.execute("INSERT INTO competition (id, format, start_year, end_year, country, international, name) " +
                        "VALUES (1, 'T_20', '2023', '2024', 'ENGLAND', true, 'Test Competition')");
        }

        // When
        var response = restTemplate.exchange(
                "http://localhost:" + port + "/api/competitions/1/games",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GameResponse>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }
}