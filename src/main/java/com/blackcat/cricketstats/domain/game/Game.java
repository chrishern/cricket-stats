package com.blackcat.cricketstats.domain.game;

import java.time.LocalDateTime;
import java.util.Objects;

public class Game {
    private Integer id;
    private Integer competitionId;
    private Integer homeTeamId;
    private Integer awayTeamId;
    private String result;
    private LocalDateTime startDateTime;

    public Game(Integer id, Integer competitionId, Integer homeTeamId, Integer awayTeamId, String result, LocalDateTime startDateTime) {
        this.id = id;
        this.competitionId = Objects.requireNonNull(competitionId, "Competition ID cannot be null");
        this.homeTeamId = Objects.requireNonNull(homeTeamId, "Home team ID cannot be null");
        this.awayTeamId = Objects.requireNonNull(awayTeamId, "Away team ID cannot be null");
        this.result = Objects.requireNonNull(result, "Result cannot be null");
        this.startDateTime = startDateTime;

        if (result.trim().isEmpty()) {
            throw new IllegalArgumentException("Result cannot be empty");
        }

        if (homeTeamId.equals(awayTeamId)) {
            throw new IllegalArgumentException("Home team and away team cannot be the same");
        }
    }

    public Integer getId() { return id; }
    public Integer getCompetitionId() { return competitionId; }
    public Integer getHomeTeamId() { return homeTeamId; }
    public Integer getAwayTeamId() { return awayTeamId; }
    public String getResult() { return result; }
    public LocalDateTime getStartDateTime() { return startDateTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}