package com.blackcat.cricketstats.domain.game;

import java.time.LocalDateTime;
import java.util.Objects;

public class GameWithTeamNames {
    private Integer id;
    private Integer competitionId;
    private Integer homeTeamId;
    private String homeTeamName;
    private Integer awayTeamId;
    private String awayTeamName;
    private String result;
    private LocalDateTime startDateTime;

    public GameWithTeamNames(Integer id, Integer competitionId, Integer homeTeamId, String homeTeamName, Integer awayTeamId, String awayTeamName, String result, LocalDateTime startDateTime) {
        this.id = id;
        this.competitionId = Objects.requireNonNull(competitionId, "Competition ID cannot be null");
        this.homeTeamId = Objects.requireNonNull(homeTeamId, "Home team ID cannot be null");
        this.homeTeamName = Objects.requireNonNull(homeTeamName, "Home team name cannot be null");
        this.awayTeamId = Objects.requireNonNull(awayTeamId, "Away team ID cannot be null");
        this.awayTeamName = Objects.requireNonNull(awayTeamName, "Away team name cannot be null");
        this.result = Objects.requireNonNull(result, "Result cannot be null");
        this.startDateTime = startDateTime;
    }

    public Integer getId() { return id; }
    public Integer getCompetitionId() { return competitionId; }
    public Integer getHomeTeamId() { return homeTeamId; }
    public String getHomeTeamName() { return homeTeamName; }
    public Integer getAwayTeamId() { return awayTeamId; }
    public String getAwayTeamName() { return awayTeamName; }
    public String getResult() { return result; }
    public LocalDateTime getStartDateTime() { return startDateTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameWithTeamNames that = (GameWithTeamNames) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}