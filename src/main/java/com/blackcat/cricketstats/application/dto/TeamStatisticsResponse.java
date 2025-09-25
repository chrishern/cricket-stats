package com.blackcat.cricketstats.application.dto;

import java.util.List;

public class TeamStatisticsResponse {
    private List<BowlingInningsResponse> bowling;
    private List<BattingInningsResponse> batting;

    public TeamStatisticsResponse() {
    }

    public TeamStatisticsResponse(List<BowlingInningsResponse> bowling, List<BattingInningsResponse> batting) {
        this.bowling = bowling;
        this.batting = batting;
    }

    public List<BowlingInningsResponse> getBowling() {
        return bowling;
    }

    public void setBowling(List<BowlingInningsResponse> bowling) {
        this.bowling = bowling;
    }

    public List<BattingInningsResponse> getBatting() {
        return batting;
    }

    public void setBatting(List<BattingInningsResponse> batting) {
        this.batting = batting;
    }
}