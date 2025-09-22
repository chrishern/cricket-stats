package com.blackcat.cricketstats.domain.battinginnings;

import java.util.Objects;

public class BattingInnings {
    private Integer id;
    private Integer gameId;
    private Integer playerId;
    private Integer runs;
    private Integer balls;
    private Integer dots;
    private Integer foursScored;
    private Integer sixesScored;
    private Integer minutesBatted;
    private Double strikeRate;

    public BattingInnings(Integer id, Integer gameId, Integer playerId, Integer runs, Integer balls,
                         Integer dots, Integer foursScored, Integer sixesScored, Integer minutesBatted,
                         Double strikeRate) {
        this.id = id;
        this.gameId = Objects.requireNonNull(gameId, "Game ID cannot be null");
        this.playerId = Objects.requireNonNull(playerId, "Player ID cannot be null");
        this.runs = Objects.requireNonNull(runs, "Runs cannot be null");
        this.balls = Objects.requireNonNull(balls, "Balls cannot be null");
        this.dots = Objects.requireNonNull(dots, "Dots cannot be null");
        this.foursScored = Objects.requireNonNull(foursScored, "Fours scored cannot be null");
        this.sixesScored = Objects.requireNonNull(sixesScored, "Sixes scored cannot be null");
        this.minutesBatted = Objects.requireNonNull(minutesBatted, "Minutes batted cannot be null");
        this.strikeRate = Objects.requireNonNull(strikeRate, "Strike rate cannot be null");

        if (runs < 0) {
            throw new IllegalArgumentException("Runs cannot be negative");
        }
        if (balls < 0) {
            throw new IllegalArgumentException("Balls cannot be negative");
        }
        if (dots < 0) {
            throw new IllegalArgumentException("Dots cannot be negative");
        }
        if (foursScored < 0) {
            throw new IllegalArgumentException("Fours scored cannot be negative");
        }
        if (sixesScored < 0) {
            throw new IllegalArgumentException("Sixes scored cannot be negative");
        }
        if (minutesBatted < 0) {
            throw new IllegalArgumentException("Minutes batted cannot be negative");
        }
        if (strikeRate < 0) {
            throw new IllegalArgumentException("Strike rate cannot be negative");
        }
    }

    public Integer getId() { return id; }
    public Integer getGameId() { return gameId; }
    public Integer getPlayerId() { return playerId; }
    public Integer getRuns() { return runs; }
    public Integer getBalls() { return balls; }
    public Integer getDots() { return dots; }
    public Integer getFoursScored() { return foursScored; }
    public Integer getSixesScored() { return sixesScored; }
    public Integer getMinutesBatted() { return minutesBatted; }
    public Double getStrikeRate() { return strikeRate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattingInnings that = (BattingInnings) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}