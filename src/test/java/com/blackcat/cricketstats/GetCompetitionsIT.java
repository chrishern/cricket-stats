package com.blackcat.cricketstats;

import com.blackcat.cricketstats.application.dto.CompetitionResponse;
import com.blackcat.cricketstats.domain.competition.Format;
import com.blackcat.cricketstats.domain.competition.Country;

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
public class GetCompetitionsIT extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldGetAllCompetitionsSuccessfully() throws Exception {
        // Given - insert test data directly into database to test ordering by start_year then name
        try (Connection conn = mysql.createConnection("");
             Statement stmt = conn.createStatement()) {

            stmt.execute("INSERT INTO competition (format, start_year, end_year, country, international, name) " +
                        "VALUES ('T_20', '2023', '2024', 'ENGLAND', true, 'Z Competition')");

            stmt.execute("INSERT INTO competition (format, start_year, end_year, country, international, name) " +
                        "VALUES ('T_20', '2022', '2023', 'ENGLAND', true, 'B Competition')");

            stmt.execute("INSERT INTO competition (format, start_year, end_year, country, international, name) " +
                        "VALUES ('T_20', '2023', '2024', 'ENGLAND', true, 'A Competition')");
        }

        // When
        var response = restTemplate.exchange(
                "http://localhost:" + port + "/api/competitions",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CompetitionResponse>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(3);

        List<CompetitionResponse> competitions = response.getBody();

        // Verify the competitions are ordered by start_year (oldest first), then by name (alphabetical)
        // First: 2022 - B Competition
        assertThat(competitions.get(0).getName()).isEqualTo("B Competition");
        assertThat(competitions.get(0).getFormat()).isEqualTo(Format.T_20);
        assertThat(competitions.get(0).getStartYear()).isEqualTo("2022");
        assertThat(competitions.get(0).getEndYear()).isEqualTo("2023");
        assertThat(competitions.get(0).getCountry()).isEqualTo(Country.ENGLAND);
        assertThat(competitions.get(0).isInternational()).isTrue();

        // Second: 2023 - A Competition (alphabetically first)
        assertThat(competitions.get(1).getName()).isEqualTo("A Competition");
        assertThat(competitions.get(1).getFormat()).isEqualTo(Format.T_20);
        assertThat(competitions.get(1).getStartYear()).isEqualTo("2023");
        assertThat(competitions.get(1).getEndYear()).isEqualTo("2024");
        assertThat(competitions.get(1).getCountry()).isEqualTo(Country.ENGLAND);
        assertThat(competitions.get(1).isInternational()).isTrue();

        // Third: 2023 - Z Competition (alphabetically second)
        assertThat(competitions.get(2).getName()).isEqualTo("Z Competition");
        assertThat(competitions.get(2).getFormat()).isEqualTo(Format.T_20);
        assertThat(competitions.get(2).getStartYear()).isEqualTo("2023");
        assertThat(competitions.get(2).getEndYear()).isEqualTo("2024");
        assertThat(competitions.get(2).getCountry()).isEqualTo(Country.ENGLAND);
        assertThat(competitions.get(2).isInternational()).isTrue();
    }

    @Test
    public void shouldReturnEmptyListWhenNoCompetitionsExist() throws Exception {
        // When
        var response = restTemplate.exchange(
                "http://localhost:" + port + "/api/competitions",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CompetitionResponse>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }
}