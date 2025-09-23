package com.blackcat.cricketstats.domain.bowlinginnings;

import java.util.Objects;

public class BowlingInnings {
    private Integer id;
    private Integer gameId;
    private Integer playerId;
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

    public BowlingInnings(Integer id, Integer gameId, Integer playerId, Integer teamId, Integer inningsOrder, Double overs, Integer maidens,
                         Integer runs, Integer wickets, Integer dots, Integer noBalls, Integer wides,
                         Integer foursConceded, Integer sixesConceded, Double economy, Double strikeRate) {
        this.id = id;
        this.gameId = Objects.requireNonNull(gameId, "Game ID cannot be null");
        this.playerId = Objects.requireNonNull(playerId, "Player ID cannot be null");
        this.teamId = Objects.requireNonNull(teamId, "Team ID cannot be null");
        this.inningsOrder = Objects.requireNonNull(inningsOrder, "Innings order cannot be null");
        this.overs = Objects.requireNonNull(overs, "Overs cannot be null");
        this.maidens = Objects.requireNonNull(maidens, "Maidens cannot be null");
        this.runs = Objects.requireNonNull(runs, "Runs cannot be null");
        this.wickets = Objects.requireNonNull(wickets, "Wickets cannot be null");
        this.dots = Objects.requireNonNull(dots, "Dots cannot be null");
        this.noBalls = Objects.requireNonNull(noBalls, "No balls cannot be null");
        this.wides = Objects.requireNonNull(wides, "Wides cannot be null");
        this.foursConceded = Objects.requireNonNull(foursConceded, "Fours conceded cannot be null");
        this.sixesConceded = Objects.requireNonNull(sixesConceded, "Sixes conceded cannot be null");
        this.economy = Objects.requireNonNull(economy, "Economy cannot be null");
        this.strikeRate = Objects.requireNonNull(strikeRate, "Strike rate cannot be null");

        if (overs < 0) {
            throw new IllegalArgumentException("Overs cannot be negative");
        }
        if (maidens < 0) {
            throw new IllegalArgumentException("Maidens cannot be negative");
        }
        if (runs < 0) {
            throw new IllegalArgumentException("Runs cannot be negative");
        }
        if (wickets < 0) {
            throw new IllegalArgumentException("Wickets cannot be negative");
        }
        if (dots < 0) {
            throw new IllegalArgumentException("Dots cannot be negative");
        }
        if (noBalls < 0) {
            throw new IllegalArgumentException("No balls cannot be negative");
        }
        if (wides < 0) {
            throw new IllegalArgumentException("Wides cannot be negative");
        }
        if (foursConceded < 0) {
            throw new IllegalArgumentException("Fours conceded cannot be negative");
        }
        if (sixesConceded < 0) {
            throw new IllegalArgumentException("Sixes conceded cannot be negative");
        }
        if (economy < 0) {
            throw new IllegalArgumentException("Economy cannot be negative");
        }
        if (strikeRate < 0) {
            throw new IllegalArgumentException("Strike rate cannot be negative");
        }
        if (inningsOrder < 0) {
            throw new IllegalArgumentException("Innings order cannot be negative");
        }
    }

    public Integer getId() { return id; }
    public Integer getGameId() { return gameId; }
    public Integer getPlayerId() { return playerId; }
    public Integer getTeamId() { return teamId; }
    public Integer getInningsOrder() { return inningsOrder; }
    public Double getOvers() { return overs; }
    public Integer getMaidens() { return maidens; }
    public Integer getRuns() { return runs; }
    public Integer getWickets() { return wickets; }
    public Integer getDots() { return dots; }
    public Integer getNoBalls() { return noBalls; }
    public Integer getWides() { return wides; }
    public Integer getFoursConceded() { return foursConceded; }
    public Integer getSixesConceded() { return sixesConceded; }
    public Double getEconomy() { return economy; }
    public Double getStrikeRate() { return strikeRate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BowlingInnings that = (BowlingInnings) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}