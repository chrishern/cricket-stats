package com.blackcat.cricketstats.application.dto;

public class BowlingInningsResponse {
    private Integer id;
    private Integer gameId;
    private Integer playerId;
    private String playerName;
    private Integer teamId;
    private Integer inningsOrder;
    private Double overs;
    private Integer maidens;
    private Integer runs;
    private Integer wickets;
    private Integer dots;
    private Integer noBalls;
    private Integer wides;
    private Integer foursConceded;
    private Integer sixesConceded;
    private Double economy;
    private Double strikeRate;

    public BowlingInningsResponse() {
    }

    public BowlingInningsResponse(Integer id, Integer gameId, Integer playerId, String playerName, Integer teamId,
                                 Integer inningsOrder, Double overs, Integer maidens, Integer runs, Integer wickets,
                                 Integer dots, Integer noBalls, Integer wides, Integer foursConceded,
                                 Integer sixesConceded, Double economy, Double strikeRate) {
        this.id = id;
        this.gameId = gameId;
        this.playerId = playerId;
        this.playerName = playerName;
        this.teamId = teamId;
        this.inningsOrder = inningsOrder;
        this.overs = overs;
        this.maidens = maidens;
        this.runs = runs;
        this.wickets = wickets;
        this.dots = dots;
        this.noBalls = noBalls;
        this.wides = wides;
        this.foursConceded = foursConceded;
        this.sixesConceded = sixesConceded;
        this.economy = economy;
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

    public Double getOvers() { return overs; }
    public void setOvers(Double overs) { this.overs = overs; }

    public Integer getMaidens() { return maidens; }
    public void setMaidens(Integer maidens) { this.maidens = maidens; }

    public Integer getRuns() { return runs; }
    public void setRuns(Integer runs) { this.runs = runs; }

    public Integer getWickets() { return wickets; }
    public void setWickets(Integer wickets) { this.wickets = wickets; }

    public Integer getDots() { return dots; }
    public void setDots(Integer dots) { this.dots = dots; }

    public Integer getNoBalls() { return noBalls; }
    public void setNoBalls(Integer noBalls) { this.noBalls = noBalls; }

    public Integer getWides() { return wides; }
    public void setWides(Integer wides) { this.wides = wides; }

    public Integer getFoursConceded() { return foursConceded; }
    public void setFoursConceded(Integer foursConceded) { this.foursConceded = foursConceded; }

    public Integer getSixesConceded() { return sixesConceded; }
    public void setSixesConceded(Integer sixesConceded) { this.sixesConceded = sixesConceded; }

    public Double getEconomy() { return economy; }
    public void setEconomy(Double economy) { this.economy = economy; }

    public Double getStrikeRate() { return strikeRate; }
    public void setStrikeRate(Double strikeRate) { this.strikeRate = strikeRate; }
}