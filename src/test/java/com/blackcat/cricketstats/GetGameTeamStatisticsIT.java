package com.blackcat.cricketstats;

import com.blackcat.cricketstats.application.dto.TeamStatisticsResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Statement;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetGameTeamStatisticsIT extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldGetTeamStatisticsSuccessfully() throws Exception {
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

            // Insert players
            stmt.execute("INSERT INTO player (id, full_name) VALUES (1, 'Player One')");
            stmt.execute("INSERT INTO player (id, full_name) VALUES (2, 'Player Two')");
            stmt.execute("INSERT INTO player (id, full_name) VALUES (3, 'Player Three')");

            // Insert game
            stmt.execute("INSERT INTO game (id, competition, home_team, away_team, result, start_date_time) " +
                        "VALUES (1, 1, 1, 2, 'Team A won by 5 wickets', '2023-06-15 14:00:00')");

            // Insert batting innings for Team A (teamId = 1)
            stmt.execute("INSERT INTO batting_innings (id, game, player, team_id, innings_order, runs, balls, dots, fours_scored, sixes_scored, minutes_batted, strike_rate, not_out) " +
                        "VALUES (1, 1, 1, 1, 1, 45, 32, 18, 4, 1, 40, 140.625, false)");
            stmt.execute("INSERT INTO batting_innings (id, game, player, team_id, innings_order, runs, balls, dots, fours_scored, sixes_scored, minutes_batted, strike_rate, not_out) " +
                        "VALUES (2, 1, 2, 1, 2, 23, 19, 12, 2, 0, 25, 121.05, false)");

            // Insert bowling innings for Team A (teamId = 1)
            stmt.execute("INSERT INTO bowling_innings (id, game, player, team_id, innings_order, overs, maidens, runs, wickets, dots, no_balls, wides, fours_conceded, sixes_conceded, economy, strike_rate) " +
                        "VALUES (1, 1, 1, 1, 1, 4.0, 1, 28, 2, 15, 0, 1, 3, 1, 7.0, 12.0)");
            stmt.execute("INSERT INTO bowling_innings (id, game, player, team_id, innings_order, overs, maidens, runs, wickets, dots, no_balls, wides, fours_conceded, sixes_conceded, economy, strike_rate) " +
                        "VALUES (2, 1, 3, 1, 2, 3.0, 0, 22, 1, 10, 1, 0, 2, 1, 7.33, 18.0)");

            // Insert some data for Team B (teamId = 2) to ensure we only get Team A data
            stmt.execute("INSERT INTO batting_innings (id, game, player, team_id, innings_order, runs, balls, dots, fours_scored, sixes_scored, minutes_batted, strike_rate, not_out) " +
                        "VALUES (3, 1, 3, 2, 1, 15, 12, 8, 1, 0, 15, 125.0, false)");
        }

        // When
        var response = restTemplate.exchange(
                "http://localhost:" + port + "/api/games/1/teams/1/statistics",
                HttpMethod.GET,
                null,
                TeamStatisticsResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        TeamStatisticsResponse statistics = response.getBody();

        // Verify batting innings - should have 2 entries for Team A, ordered by innings_order
        assertThat(statistics.getBatting()).hasSize(2);
        assertThat(statistics.getBatting().get(0).getId()).isEqualTo(1);
        assertThat(statistics.getBatting().get(0).getPlayerId()).isEqualTo(1);
        assertThat(statistics.getBatting().get(0).getTeamId()).isEqualTo(1);
        assertThat(statistics.getBatting().get(0).getRuns()).isEqualTo(45);
        assertThat(statistics.getBatting().get(0).getBalls()).isEqualTo(32);
        assertThat(statistics.getBatting().get(0).getInningsOrder()).isEqualTo(1);

        assertThat(statistics.getBatting().get(1).getId()).isEqualTo(2);
        assertThat(statistics.getBatting().get(1).getPlayerId()).isEqualTo(2);
        assertThat(statistics.getBatting().get(1).getTeamId()).isEqualTo(1);
        assertThat(statistics.getBatting().get(1).getRuns()).isEqualTo(23);
        assertThat(statistics.getBatting().get(1).getBalls()).isEqualTo(19);
        assertThat(statistics.getBatting().get(1).getInningsOrder()).isEqualTo(2);

        // Verify bowling innings - should have 2 entries for Team A, ordered by innings_order
        assertThat(statistics.getBowling()).hasSize(2);
        assertThat(statistics.getBowling().get(0).getId()).isEqualTo(1);
        assertThat(statistics.getBowling().get(0).getPlayerId()).isEqualTo(1);
        assertThat(statistics.getBowling().get(0).getTeamId()).isEqualTo(1);
        assertThat(statistics.getBowling().get(0).getOvers()).isEqualTo(4.0);
        assertThat(statistics.getBowling().get(0).getWickets()).isEqualTo(2);
        assertThat(statistics.getBowling().get(0).getInningsOrder()).isEqualTo(1);

        assertThat(statistics.getBowling().get(1).getId()).isEqualTo(2);
        assertThat(statistics.getBowling().get(1).getPlayerId()).isEqualTo(3);
        assertThat(statistics.getBowling().get(1).getTeamId()).isEqualTo(1);
        assertThat(statistics.getBowling().get(1).getOvers()).isEqualTo(3.0);
        assertThat(statistics.getBowling().get(1).getWickets()).isEqualTo(1);
        assertThat(statistics.getBowling().get(1).getInningsOrder()).isEqualTo(2);
    }

    @Test
    public void shouldReturnEmptyListsWhenNoStatisticsExistForTeam() throws Exception {
        // Given - insert game but no statistics for the queried team
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

            // Insert game
            stmt.execute("INSERT INTO game (id, competition, home_team, away_team, result, start_date_time) " +
                        "VALUES (1, 1, 1, 2, 'Team A won by 5 wickets', '2023-06-15 14:00:00')");
        }

        // When
        var response = restTemplate.exchange(
                "http://localhost:" + port + "/api/games/1/teams/1/statistics",
                HttpMethod.GET,
                null,
                TeamStatisticsResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        TeamStatisticsResponse statistics = response.getBody();
        assertThat(statistics.getBatting()).isEmpty();
        assertThat(statistics.getBowling()).isEmpty();
    }
}