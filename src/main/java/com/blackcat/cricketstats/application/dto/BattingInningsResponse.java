package com.blackcat.cricketstats.application.dto;

public class BattingInningsResponse {
    private Integer id;
    private Integer gameId;
    private Integer playerId;
    private String playerName;
    private Integer teamId;
    private Integer inningsOrder;
    private Integer runs;
    private Integer balls;
    private Integer dots;
    private Integer foursScored;
    private Integer sixesScored;
    private Integer minutesBatted;
    private Double strikeRate;

    public BattingInningsResponse() {
    }

    public BattingInningsResponse(Integer id, Integer gameId, Integer playerId, String playerName, Integer teamId,
                                 Integer inningsOrder, Integer runs, Integer balls, Integer dots,
                                 Integer foursScored, Integer sixesScored, Integer minutesBatted, Double strikeRate) {
        this.id = id;
        this.gameId = gameId;
        this.playerId = playerId;
        this.playerName = playerName;
        this.teamId = teamId;
        this.inningsOrder = inningsOrder;
        this.runs = runs;
        this.balls = balls;
        this.dots = dots;
        this.foursScored = foursScored;
        this.sixesScored = sixesScored;
        this.minutesBatted = minutesBatted;
        this.strikeRate = strikeRate;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getGameId() { return gameId; }
    public void setGameId(Integer gameId) { this.gameId = gameId; }

    public Integer getPlayerId() { return playerId; }
    public void setPlayerId(Integer playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public Integer getInningsOrder() { return inningsOrder; }
    public void setInningsOrder(Integer inningsOrder) { this.inningsOrder = inningsOrder; }

    public Integer getRuns() { return runs; }
    public void setRuns(Integer runs) { this.runs = runs; }

    public Integer getBalls() { return balls; }
    public void setBalls(Integer balls) { this.balls = balls; }

    public Integer getDots() { return dots; }
    public void setDots(Integer dots) { this.dots = dots; }

    public Integer getFoursScored() { return foursScored; }
    public void setFoursScored(Integer foursScored) { this.foursScored = foursScored; }

    public Integer getSixesScored() { return sixesScored; }
    public void setSixesScored(Integer sixesScored) { this.sixesScored = sixesScored; }

    public Integer getMinutesBatted() { return minutesBatted; }
    public void setMinutesBatted(Integer minutesBatted) { this.minutesBatted = minutesBatted; }

    public Double getStrikeRate() { return strikeRate; }
    public void setStrikeRate(Double strikeRate) { this.strikeRate = strikeRate; }
}