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
    }
}