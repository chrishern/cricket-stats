package com.blackcat.cricketstats.domain.competition;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CompetitionTest {

    @Test
    public void shouldCreateCompetition_whenAllInputsAreValid() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2023";
        String endYear = "2024";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When
        Competition competition = new Competition(id, format, startYear, endYear, country, international, name);

        // Then
        assertThat(competition.getId()).isEqualTo(id);
        assertThat(competition.getFormat()).isEqualTo(format);
        assertThat(competition.getStartYear()).isEqualTo(startYear);
        assertThat(competition.getEndYear()).isEqualTo(endYear);
        assertThat(competition.getCountry()).isEqualTo(country);
        assertThat(competition.isInternational()).isEqualTo(international);
        assertThat(competition.getName()).isEqualTo(name);
    }

    @Test
    public void shouldCreateCompetition_whenInternationalAndCountryIsNull() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2023";
        String endYear = "2024";
        Country country = null;
        boolean international = true;
        String name = "International Competition";

        // When
        Competition competition = new Competition(id, format, startYear, endYear, country, international, name);

        // Then
        assertThat(competition.getId()).isEqualTo(id);
        assertThat(competition.getFormat()).isEqualTo(format);
        assertThat(competition.getStartYear()).isEqualTo(startYear);
        assertThat(competition.getEndYear()).isEqualTo(endYear);
        assertThat(competition.getCountry()).isNull();
        assertThat(competition.isInternational()).isTrue();
        assertThat(competition.getName()).isEqualTo(name);
    }

    @Test
    public void shouldCreateCompetition_whenStartYearEqualsEndYear() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2023";
        String endYear = "2023";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Single Year Competition";

        // When
        Competition competition = new Competition(id, format, startYear, endYear, country, international, name);

        // Then
        assertThat(competition.getStartYear()).isEqualTo("2023");
        assertThat(competition.getEndYear()).isEqualTo("2023");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowNullPointerException_whenFormatIsNull() {
        // Given
        Integer id = 1;
        Format format = null;
        String startYear = "2023";
        String endYear = "2024";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Format cannot be null");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowNullPointerException_whenStartYearIsNull() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = null;
        String endYear = "2024";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Start year cannot be null");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowNullPointerException_whenEndYearIsNull() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2023";
        String endYear = null;
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("End year cannot be null");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowNullPointerException_whenNameIsNull() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2023";
        String endYear = "2024";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = null;

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Name cannot be null");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowIllegalArgumentException_whenNonInternationalAndCountryIsNull() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2023";
        String endYear = "2024";
        Country country = null;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Country is required when international is false");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowIllegalArgumentException_whenStartYearIsInvalid() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "invalid";
        String endYear = "2024";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Years must be valid numbers");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowIllegalArgumentException_whenEndYearIsInvalid() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2023";
        String endYear = "invalid";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Years must be valid numbers");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowIllegalArgumentException_whenStartYearIsTooEarly() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "1799";
        String endYear = "2024";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Years must be between 1800 and 3000");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowIllegalArgumentException_whenStartYearIsTooLate() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "3001";
        String endYear = "3002";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Years must be between 1800 and 3000");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowIllegalArgumentException_whenEndYearIsTooEarly() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2023";
        String endYear = "1799";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Years must be between 1800 and 3000");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowIllegalArgumentException_whenEndYearIsTooLate() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2023";
        String endYear = "3001";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Years must be between 1800 and 3000");
    }

    @Test
    public void shouldNotCreateCompetition_andThrowIllegalArgumentException_whenStartYearIsAfterEndYear() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "2025";
        String endYear = "2024";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Test Competition";

        // When & Then
        assertThatThrownBy(() -> new Competition(id, format, startYear, endYear, country, international, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start year cannot be after end year");
    }

    @Test
    public void shouldCreateCompetition_whenYearsAreAtBoundaryValues() {
        // Given
        Integer id = 1;
        Format format = Format.T_20;
        String startYear = "1800";
        String endYear = "3000";
        Country country = Country.ENGLAND;
        boolean international = false;
        String name = "Boundary Test Competition";

        // When
        Competition competition = new Competition(id, format, startYear, endYear, country, international, name);

        // Then
        assertThat(competition.getStartYear()).isEqualTo("1800");
        assertThat(competition.getEndYear()).isEqualTo("3000");
    }
}