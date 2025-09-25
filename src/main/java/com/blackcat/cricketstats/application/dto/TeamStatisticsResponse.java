package com.blackcat.cricketstats.application.dto;

import com.blackcat.cricketstats.domain.battinginnings.BattingInnings;
import com.blackcat.cricketstats.domain.bowlinginnings.BowlingInnings;

import java.util.List;

public class TeamStatisticsResponse {
    private List<BowlingInnings> bowling;
    private List<BattingInnings> batting;

    public TeamStatisticsResponse() {
    }

    public TeamStatisticsResponse(List<BowlingInnings> bowling, List<BattingInnings> batting) {
        this.bowling = bowling;
        this.batting = batting;
    }

    public List<BowlingInnings> getBowling() {
        return bowling;
    }

    public void setBowling(List<BowlingInnings> bowling) {
        this.bowling = bowling;
    }

    public List<BattingInnings> getBatting() {
        return batting;
    }

    public void setBatting(List<BattingInnings> batting) {
        this.batting = batting;
    }
}