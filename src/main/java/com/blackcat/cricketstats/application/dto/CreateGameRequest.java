package com.blackcat.cricketstats.application.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateGameRequest {
    @NotBlank(message = "Scorecard URL is required")
    private String scorecardUrl;

    public String getScorecardUrl() { return scorecardUrl; }
    public void setScorecardUrl(String scorecardUrl) { this.scorecardUrl = scorecardUrl; }
}