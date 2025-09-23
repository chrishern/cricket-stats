package com.blackcat.cricketstats.application.dto;

import java.time.LocalDateTime;

public class GameResponse {
    private Integer id;
    private String homeTeamName;
    private String awayTeamName;
    private LocalDateTime startDateTime;
    private String result;

    public GameResponse() {
    }

    public GameResponse(Integer id, String homeTeamName, String awayTeamName, LocalDateTime startDateTime, String result) {
        this.id = id;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.startDateTime = startDateTime;
        this.result = result;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getHomeTeamName() { return homeTeamName; }
    public void setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }

    public String getAwayTeamName() { return awayTeamName; }
    public void setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}