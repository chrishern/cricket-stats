package com.blackcat.cricketstats.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ScorecardScrapingServiceTest {

    private final ScorecardScrapingService service = new ScorecardScrapingService();

    @Test
    public void shouldParseDurhamVsWorcestershireMatchDrawn() throws Exception {
        File sampleFile = new ClassPathResource("samples/dur-worcs-sample-scorecard.html").getFile();
        String filePath = sampleFile.getAbsolutePath();

        ScorecardScrapingService.ScorecardData result = service.scrapeScorecard(filePath);

        assertThat(result.getHomeTeam()).isEqualTo("Durham");
        assertThat(result.getAwayTeam()).isEqualTo("Worcestershire");
        assertThat(result.getResult()).isEqualTo("Match Drawn");
        assertThat(result.getCompetitionName()).isEqualTo("Rothesay County Championship Division 1");
        assertThat(result.getStartDateTime()).isEqualTo("2025-09-15T09:30:00.000Z");
        assertThat(result.getHomeTeamId()).isEqualTo(1412);
        assertThat(result.getAwayTeamId()).isEqualTo(1634);

        // Assert home team players (Durham)
        assertThat(result.getHomeTeamPlayers()).hasSize(11);
        assertThat(result.getHomeTeamPlayers().get(0).getId()).isEqualTo(12735);
        assertThat(result.getHomeTeamPlayers().get(0).getDisplayName()).isEqualTo("Alex Lees");
        assertThat(result.getHomeTeamPlayers().get(1).getId()).isEqualTo(71461);
        assertThat(result.getHomeTeamPlayers().get(1).getDisplayName()).isEqualTo("Emilio Gay");

        // Assert away team players (Worcestershire)
        assertThat(result.getAwayTeamPlayers()).hasSize(11);
        assertThat(result.getAwayTeamPlayers().get(0).getId()).isEqualTo(30403);
        assertThat(result.getAwayTeamPlayers().get(0).getDisplayName()).isEqualTo("Jake Libby");
    }

    @Test
    public void shouldParseSomersetVsHampshireMatchDrawn() throws Exception {
        File sampleFile = new ClassPathResource("samples/som-hants-sample-scorecard.html").getFile();
        String filePath = sampleFile.getAbsolutePath();

        ScorecardScrapingService.ScorecardData result = service.scrapeScorecard(filePath);

        assertThat(result.getHomeTeam()).isEqualTo("Somerset");
        assertThat(result.getAwayTeam()).isEqualTo("Hampshire");
        assertThat(result.getResult()).isEqualTo("Match Drawn");
        assertThat(result.getCompetitionName()).isEqualTo("Rothesay County Championship Division 1");
        assertThat(result.getStartDateTime()).isEqualTo("2025-09-15T09:30:00.000Z");
        assertThat(result.getHomeTeamId()).isEqualTo(1451);
        assertThat(result.getAwayTeamId()).isEqualTo(1446);
    }

    @Test
    public void shouldParseBattingInningsDataFromSomersetVsHampshire() throws Exception {
        File sampleFile = new ClassPathResource("samples/som-hants-sample-scorecard.html").getFile();
        String filePath = sampleFile.getAbsolutePath();

        ScorecardScrapingService.ScorecardData result = service.scrapeScorecard(filePath);

        // Verify that batting innings data is extracted
        // Based on the JSON data and team rosters, there should be 30 batting innings records
        // (filtering from 42 total batting entries to only those players in team rosters)
        assertThat(result.getBattingInnings()).hasSize(30);

        // Find a specific batting innings entry to test
        // Based on the JSON data, Tom Kohler-Cadmore (playerId: 15650) should have this record:
        // "runs": "10", "balls": "10", "dots": "6", "fours": "2", "sixes": "0", "minutes": "16", "strikeRate": "100.00", "isOut": true
        var kohlerCadmoreBattingInnings = result.getBattingInnings().stream()
            .filter(innings -> innings.getPlayerId().equals(15650))
            .findFirst();

        assertThat(kohlerCadmoreBattingInnings).isPresent();

        var kohlerInnings = kohlerCadmoreBattingInnings.get();
        assertThat(kohlerInnings.getRuns()).isEqualTo(10);
        assertThat(kohlerInnings.getBalls()).isEqualTo(10);
        assertThat(kohlerInnings.getDots()).isEqualTo(6);
        assertThat(kohlerInnings.getFours()).isEqualTo(2);
        assertThat(kohlerInnings.getSixes()).isEqualTo(0);
        assertThat(kohlerInnings.getMinutes()).isEqualTo(16);
        assertThat(kohlerInnings.getStrikeRate()).isEqualTo(100.00);
        assertThat(kohlerInnings.getIsOut()).isTrue();

        // Test another player - Ali Orr (playerId: 73795) should have an innings in the 2nd innings:
        // "runs": "8", "balls": "24", "dots": "20", "fours": "1", "sixes": "0", "minutes": "23", "strikeRate": "33.33", "isOut": true
        var orrBattingInnings = result.getBattingInnings().stream()
            .filter(battingInnings -> battingInnings.getPlayerId().equals(73795) && battingInnings.getRuns().equals(8))
            .findFirst();

        assertThat(orrBattingInnings).isPresent();

        var orrInnings = orrBattingInnings.get();
        assertThat(orrInnings.getRuns()).isEqualTo(8);
        assertThat(orrInnings.getBalls()).isEqualTo(24);
        assertThat(orrInnings.getDots()).isEqualTo(20);
        assertThat(orrInnings.getFours()).isEqualTo(1);
        assertThat(orrInnings.getSixes()).isEqualTo(0);
        assertThat(orrInnings.getMinutes()).isEqualTo(23);
        assertThat(orrInnings.getStrikeRate()).isEqualTo(33.33);
        assertThat(orrInnings.getIsOut()).isTrue();
    }

    @Test
    public void shouldParseBowlingInningsDataFromDurhamVsWorcestershire() throws Exception {
        File sampleFile = new ClassPathResource("samples/dur-worcs-sample-scorecard.html").getFile();
        String filePath = sampleFile.getAbsolutePath();

        ScorecardScrapingService.ScorecardData result = service.scrapeScorecard(filePath);

        // Verify that bowling innings data is extracted
        // Based on the Durham vs Worcestershire JSON data and team rosters, there should be 15 bowling innings records
        // (filtering from 15 total bowling entries to only those players in team rosters)
        assertThat(result.getBowlingInnings()).hasSize(15);

        // Find a specific bowling innings entry to test
        // Based on the JSON data, Ben Raine (playerId: 17306) should have this record:
        // balls=198, overs=33.0, maidens=7, runs=81, wickets=2, dots=151, noBalls=0, wides=0, foursConceded=9, sixesConceded=0, economy=2.45
        // strikeRate should be calculated as balls/wickets = 198/2 = 99.0
        var benRaineBowlingInnings = result.getBowlingInnings().stream()
            .filter(innings -> innings.getPlayerId().equals(17306))
            .findFirst();

        assertThat(benRaineBowlingInnings).isPresent();

        var innings = benRaineBowlingInnings.get();
        assertThat(innings.getOvers()).isEqualTo(33.0);
        assertThat(innings.getMaidens()).isEqualTo(7);
        assertThat(innings.getRuns()).isEqualTo(81);
        assertThat(innings.getWickets()).isEqualTo(2);
        assertThat(innings.getDots()).isEqualTo(151);
        assertThat(innings.getNoBalls()).isEqualTo(0);
        assertThat(innings.getWides()).isEqualTo(0);
        assertThat(innings.getFoursConceded()).isEqualTo(9);
        assertThat(innings.getSixesConceded()).isEqualTo(0);
        assertThat(innings.getEconomy()).isEqualTo(2.45);
        assertThat(innings.getStrikeRate()).isEqualTo(99.0);
    }

    @Test
    public void shouldParseSurreyVsNottinghamshireWinByRuns() throws Exception {
        File sampleFile = new ClassPathResource("samples/surrey-notts-sample-scorecard.html").getFile();
        String filePath = sampleFile.getAbsolutePath();

        ScorecardScrapingService.ScorecardData result = service.scrapeScorecard(filePath);

        assertThat(result.getHomeTeam()).isEqualTo("Surrey");
        assertThat(result.getAwayTeam()).isEqualTo("Nottinghamshire");
        assertThat(result.getResult()).isEqualTo("Nottinghamshire win by 20 runs");
        assertThat(result.getCompetitionName()).isEqualTo("Rothesay County Championship Division 1");
        assertThat(result.getStartDateTime()).isEqualTo("2025-09-15T09:30:00.000Z");
        assertThat(result.getHomeTeamId()).isEqualTo(1707);
        assertThat(result.getAwayTeamId()).isEqualTo(1121);
    }

    @Test
    public void shouldParseGloucestershireVsNorthamptonshireWinByWickets() throws Exception {
        File sampleFile = new ClassPathResource("samples/glos-northants-sample-scorecard.html").getFile();
        String filePath = sampleFile.getAbsolutePath();

        ScorecardScrapingService.ScorecardData result = service.scrapeScorecard(filePath);

        assertThat(result.getHomeTeam()).isEqualTo("Gloucestershire");
        assertThat(result.getAwayTeam()).isEqualTo("Northamptonshire");
        assertThat(result.getResult()).isEqualTo("Gloucestershire win by 7 wickets");
        assertThat(result.getCompetitionName()).isEqualTo("Rothesay County Championship Division 2");
        assertThat(result.getStartDateTime()).isEqualTo("2025-09-15T09:30:00.000Z");
        assertThat(result.getHomeTeamId()).isEqualTo(2128);
        assertThat(result.getAwayTeamId()).isEqualTo(1602);
    }
}