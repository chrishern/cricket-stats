package com.blackcat.cricketstats.application.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ScorecardScrapingService {

    public ScorecardData scrapeScorecard(String url) {
        try {
            Document doc;
            if (url.startsWith("file://") || url.startsWith("/")) {
                String filePath = url.startsWith("file://") ? url.substring(7) : url;
                doc = Jsoup.parse(new java.io.File(filePath));
            } else {
                doc = Jsoup.connect(url).get();
            }

            String[] teamNames = extractTeamNames(doc);
            String result = extractMatchResult(doc);

            return new ScorecardData(teamNames[0], teamNames[1], result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to scrape scorecard from URL: " + url, e);
        }
    }

    private String[] extractTeamNames(Document doc) {
        String title = doc.title();

        if (title.contains(" v ")) {
            String teamsPart = title.split(" - ")[0];
            String[] teams = teamsPart.split(" v ");
            if (teams.length == 2) {
                return new String[]{teams[0].trim(), teams[1].trim()};
            }
        }

        if (title.contains(" vs ")) {
            String teamsPart = title.split(" - ")[0];
            String[] teams = teamsPart.split(" vs ");
            if (teams.length == 2) {
                return new String[]{teams[0].trim(), teams[1].trim()};
            }
        }

        throw new RuntimeException("Could not extract team names from scorecard");
    }

    private String extractMatchResult(Document doc) {
        String htmlContent = doc.html();

        String[] resultPatterns = {"Match Drawn", "Match Won", "Match Tied", "No Result", "Abandoned"};

        for (String pattern : resultPatterns) {
            if (htmlContent.contains(">" + pattern + "</")) {
                return pattern;
            }
            if (htmlContent.contains("\"" + pattern + "\"")) {
                return pattern;
            }
            if (htmlContent.contains(pattern)) {
                return pattern;
            }
        }

        throw new RuntimeException("Could not extract match result from scorecard");
    }

    public static class ScorecardData {
        private final String homeTeam;
        private final String awayTeam;
        private final String result;

        public ScorecardData(String homeTeam, String awayTeam, String result) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.result = result;
        }

        public String getHomeTeam() { return homeTeam; }
        public String getAwayTeam() { return awayTeam; }
        public String getResult() { return result; }
    }
}