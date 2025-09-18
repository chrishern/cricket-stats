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
        String htmlContent = doc.html();

        String[] teamsFromJson = extractTeamNamesFromJson(htmlContent);
        if (teamsFromJson != null) {
            return teamsFromJson;
        }

        return extractTeamNamesFromTitle(doc.title());
    }

    private String[] extractTeamNamesFromJson(String htmlContent) {
        String htmlTail = htmlContent.length() > 50000 ?
            htmlContent.substring(htmlContent.length() - 50000) : htmlContent;

        String homeTeamPattern = "\"homeTeam\":\\{[^}]*\"name\":\\{[^}]*\"fullName\":\"([^\"]*)\"[^}]*\\}";
        String awayTeamPattern = "\"awayTeam\":\\{[^}]*\"name\":\\{[^}]*\"fullName\":\"([^\"]*)\"[^}]*\\}";

        java.util.regex.Pattern homePattern = java.util.regex.Pattern.compile(homeTeamPattern);
        java.util.regex.Pattern awayPattern = java.util.regex.Pattern.compile(awayTeamPattern);

        java.util.regex.Matcher homeMatcher = homePattern.matcher(htmlTail);
        java.util.regex.Matcher awayMatcher = awayPattern.matcher(htmlTail);

        if (homeMatcher.find() && awayMatcher.find()) {
            return new String[]{homeMatcher.group(1), awayMatcher.group(1)};
        }

        return null;
    }

    private String[] extractTeamNamesFromTitle(String title) {
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

        String resultFromJson = extractMatchResultFromJson(htmlContent);
        if (resultFromJson != null) {
            return resultFromJson;
        }

        String resultFromContent = extractMatchResultFromContent(htmlContent);
        if (resultFromContent != null) {
            return resultFromContent;
        }

        throw new RuntimeException("Could not extract match result from scorecard");
    }

    private String extractMatchResultFromContent(String htmlContent) {
        java.util.regex.Pattern winByRunsPattern = java.util.regex.Pattern.compile("([A-Za-z\\s]+)\\s+win\\s+by\\s+(\\d+)\\s+runs?");
        java.util.regex.Matcher runsMatcher = winByRunsPattern.matcher(htmlContent);
        if (runsMatcher.find()) {
            return runsMatcher.group(1).trim() + " win by " + runsMatcher.group(2) + " runs";
        }

        java.util.regex.Pattern winByWicketsPattern = java.util.regex.Pattern.compile("([A-Za-z\\s]+)\\s+win\\s+by\\s+(\\d+)\\s+wickets?");
        java.util.regex.Matcher wicketsMatcher = winByWicketsPattern.matcher(htmlContent);
        if (wicketsMatcher.find()) {
            return wicketsMatcher.group(1).trim() + " win by " + wicketsMatcher.group(2) + " wickets";
        }

        if (htmlContent.toLowerCase().contains("match drawn")) {
            return "Match Drawn";
        }

        if (htmlContent.toLowerCase().contains("match tied")) {
            return "Match Tied";
        }

        if (htmlContent.toLowerCase().contains("no result")) {
            return "No Result";
        }

        if (htmlContent.toLowerCase().contains("abandoned")) {
            return "Abandoned";
        }

        return null;
    }

    private String extractMatchResultFromJson(String htmlContent) {
        String htmlTail = htmlContent.length() > 50000 ?
            htmlContent.substring(htmlContent.length() - 50000) : htmlContent;

        String resultPattern = "\"resultString\":\"([^\"]*)\"";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(resultPattern);
        java.util.regex.Matcher matcher = pattern.matcher(htmlTail);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
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