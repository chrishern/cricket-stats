package com.blackcat.cricketstats.application.dto;

import java.time.LocalDateTime;

public class GameResponse {
    private Integer id;
    private Integer homeTeamId;
    private String homeTeamName;
    private Integer awayTeamId;
    private String awayTeamName;
    private LocalDateTime startDateTime;
    private String result;

    public GameResponse() {
    }

    public GameResponse(Integer id, Integer homeTeamId, String homeTeamName, Integer awayTeamId, String awayTeamName, LocalDateTime startDateTime, String result) {
        this.id = id;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;
        this.startDateTime = startDateTime;
        this.result = result;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(Integer homeTeamId) { this.homeTeamId = homeTeamId; }

    public String getHomeTeamName() { return homeTeamName; }
    public void setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }

    public Integer getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(Integer awayTeamId) { this.awayTeamId = awayTeamId; }

    public String getAwayTeamName() { return awayTeamName; }
    public void setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}