package com.blackcat.cricketstats.domain.game;

import java.time.LocalDateTime;
import java.util.Objects;

public class GameWithTeamNames {
    private Integer id;
    private Integer competitionId;
    private String homeTeamName;
    private String awayTeamName;
    private String result;
    private LocalDateTime startDateTime;

    public GameWithTeamNames(Integer id, Integer competitionId, String homeTeamName, String awayTeamName, String result, LocalDateTime startDateTime) {
        this.id = id;
        this.competitionId = Objects.requireNonNull(competitionId, "Competition ID cannot be null");
        this.homeTeamName = Objects.requireNonNull(homeTeamName, "Home team name cannot be null");
        this.awayTeamName = Objects.requireNonNull(awayTeamName, "Away team name cannot be null");
        this.result = Objects.requireNonNull(result, "Result cannot be null");
        this.startDateTime = startDateTime;
    }

    public Integer getId() { return id; }
    public Integer getCompetitionId() { return competitionId; }
    public String getHomeTeamName() { return homeTeamName; }
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