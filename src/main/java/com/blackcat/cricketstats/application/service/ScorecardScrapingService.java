package com.blackcat.cricketstats.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ScorecardScrapingService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScorecardData scrapeScorecard(String url) {
        try {
            Document doc;
            if (url.startsWith("file://") || url.startsWith("/")) {
                String filePath = url.startsWith("file://") ? url.substring(7) : url;
                doc = Jsoup.parse(new java.io.File(filePath));
            } else {
                doc = Jsoup.connect(url).get();
            }

            ScorecardData fromJson = extractFromJson(doc);
            if (fromJson != null) {
                return fromJson;
            }

            throw new RuntimeException("Could not find window.__INITIAL_DATA__ script tag or extract scorecard data from JSON");

        } catch (Exception e) {
            throw new RuntimeException("Failed to scrape scorecard from URL: " + url, e);
        }
    }

    private ScorecardData extractFromJson(Document doc) {
        try {
            Elements scriptElements = doc.select("script");
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                if (scriptContent.contains("window.__INITIAL_DATA__=")) {
                    String jsonStr = extractJsonString(scriptContent);
                    if (jsonStr != null) {
                        return parseJsonData(jsonStr);
                    }
                }
            }
        } catch (Exception e) {
            // Fall through to return null
        }
        return null;
    }

    private String extractJsonString(String scriptContent) {
        int startIndex = scriptContent.indexOf("window.__INITIAL_DATA__=");
        if (startIndex == -1) {
            return null;
        }

        startIndex += "window.__INITIAL_DATA__=".length();

        if (startIndex >= scriptContent.length() || scriptContent.charAt(startIndex) != '"') {
            return null;
        }

        // Skip the opening quote
        startIndex++;

        StringBuilder jsonBuilder = new StringBuilder();
        boolean inEscape = false;

        for (int i = startIndex; i < scriptContent.length(); i++) {
            char c = scriptContent.charAt(i);

            if (inEscape) {
                // Handle escaped characters and unescape them
                switch (c) {
                    case '"':
                        jsonBuilder.append('"');
                        break;
                    case '\\':
                        jsonBuilder.append('\\');
                        break;
                    case '/':
                        jsonBuilder.append('/');
                        break;
                    case 'b':
                        jsonBuilder.append('\b');
                        break;
                    case 'f':
                        jsonBuilder.append('\f');
                        break;
                    case 'n':
                        jsonBuilder.append('\n');
                        break;
                    case 'r':
                        jsonBuilder.append('\r');
                        break;
                    case 't':
                        jsonBuilder.append('\t');
                        break;
                    case 'u':
                        // Unicode escape - read the next 4 hex digits
                        if (i + 4 < scriptContent.length()) {
                            String hexDigits = scriptContent.substring(i + 1, i + 5);
                            try {
                                int codePoint = Integer.parseInt(hexDigits, 16);
                                jsonBuilder.append((char) codePoint);
                                i += 4; // Skip the 4 hex digits
                            } catch (NumberFormatException e) {
                                // Invalid unicode escape, just add the characters as-is
                                jsonBuilder.append("\\u").append(hexDigits);
                                i += 4;
                            }
                        } else {
                            jsonBuilder.append("\\u");
                        }
                        break;
                    default:
                        // Unknown escape, keep the backslash
                        jsonBuilder.append('\\').append(c);
                        break;
                }
                inEscape = false;
                continue;
            }

            if (c == '\\') {
                inEscape = true;
                continue;
            }

            if (c == '"') {
                // End of JSON string
                break;
            }

            jsonBuilder.append(c);
        }

        return jsonBuilder.toString();
    }

    private ScorecardData parseJsonData(String jsonStr) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            JsonNode dataNode = rootNode.get("data");
            if (dataNode == null) {
                return null;
            }

            JsonNode sportHeaderNode = null;
            JsonNode cricketScorecardNode = null;

            var fieldNames = dataNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                if (fieldName.startsWith("sport-header")) {
                    sportHeaderNode = dataNode.get(fieldName);
                } else if (fieldName.startsWith("cricket-scorecard")) {
                    cricketScorecardNode = dataNode.get(fieldName);
                }
            }

            if (sportHeaderNode == null || cricketScorecardNode == null) {
                return null;
            }

            JsonNode sportHeaderData = sportHeaderNode.get("data");
            JsonNode cricketScorecardData = cricketScorecardNode.get("data");

            if (sportHeaderData == null || cricketScorecardData == null) {
                return null;
            }

            JsonNode participants = sportHeaderData.get("participants");
            JsonNode match = cricketScorecardData.get("match");

            if (participants == null || match == null) {
                return null;
            }

            JsonNode homeTeam = participants.get("homeTeam");
            JsonNode awayTeam = participants.get("awayTeam");

            if (homeTeam == null || awayTeam == null) {
                return null;
            }

            String homeTeamName = homeTeam.get("name").asText();
            String awayTeamName = awayTeam.get("name").asText();
            String resultString = match.get("resultString").asText();

            return new ScorecardData(homeTeamName, awayTeamName, resultString);

        } catch (Exception e) {
            return null;
        }
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