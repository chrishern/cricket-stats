package com.blackcat.cricketstats.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

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

            JsonNode tournamentNameNode = sportHeaderData.get("tournamentName");
            String competitionName = tournamentNameNode != null ? tournamentNameNode.asText() : null;

            JsonNode startDateTimeNode = match.get("startDateTime");
            String startDateTime = startDateTimeNode != null ? startDateTimeNode.asText() : null;

            JsonNode homeTeamIdNode = homeTeam.get("id");
            Integer homeTeamId = homeTeamIdNode != null ? homeTeamIdNode.asInt() : null;

            JsonNode awayTeamIdNode = awayTeam.get("id");
            Integer awayTeamId = awayTeamIdNode != null ? awayTeamIdNode.asInt() : null;

            List<PlayerData> homeTeamPlayers = extractPlayers(cricketScorecardData.get("homeTeam"));
            List<PlayerData> awayTeamPlayers = extractPlayers(cricketScorecardData.get("awayTeam"));

            List<BattingInningsData> battingInnings = extractBattingInnings(cricketScorecardData, homeTeamId, homeTeamPlayers, awayTeamId, awayTeamPlayers);
            List<BowlingInningsData> bowlingInnings = extractBowlingInnings(cricketScorecardData, homeTeamId, homeTeamPlayers, awayTeamId, awayTeamPlayers);

            return new ScorecardData(homeTeamName, awayTeamName, resultString, competitionName, startDateTime, homeTeamId, awayTeamId, homeTeamPlayers, awayTeamPlayers, battingInnings, bowlingInnings);

        } catch (Exception e) {
            return null;
        }
    }

    private List<PlayerData> extractPlayers(JsonNode teamNode) {
        List<PlayerData> players = new ArrayList<>();
        if (teamNode == null) {
            return players;
        }

        JsonNode playersNode = teamNode.get("players");
        if (playersNode == null) {
            return players;
        }

        JsonNode startersNode = playersNode.get("starters");
        if (startersNode != null && startersNode.isArray()) {
            for (JsonNode playerNode : startersNode) {
                String idStr = playerNode.get("id").asText();
                String displayName = playerNode.get("displayName").asText();
                try {
                    Integer playerId = Integer.parseInt(idStr);
                    players.add(new PlayerData(playerId, displayName));
                } catch (NumberFormatException e) {
                    // Skip players with invalid IDs
                }
            }
        }

        return players;
    }

    private List<BattingInningsData> extractBattingInnings(JsonNode cricketScorecardData, Integer homeTeamId, List<PlayerData> homeTeamPlayers, Integer awayTeamId, List<PlayerData> awayTeamPlayers) {
        List<BattingInningsData> battingInnings = new ArrayList<>();

        JsonNode inningsNode = cricketScorecardData.get("innings");
        if (inningsNode == null || !inningsNode.isArray()) {
            return battingInnings;
        }

        // Create a map of all players for quick lookup
        var allPlayers = new ArrayList<PlayerData>();
        allPlayers.addAll(homeTeamPlayers);
        allPlayers.addAll(awayTeamPlayers);

        for (JsonNode inning : inningsNode) {
            JsonNode battingNode = inning.get("batting");
            if (battingNode != null && battingNode.isArray()) {
                for (JsonNode battingEntry : battingNode) {
                    try {
                        JsonNode playerIdNode = battingEntry.get("playerId");
                        if (playerIdNode == null) {
                            continue;
                        }

                        Integer playerId = Integer.parseInt(playerIdNode.asText());

                        // Determine which team this player belongs to
                        Integer teamId = null;
                        if (homeTeamPlayers.stream().anyMatch(player -> player.getId().equals(playerId))) {
                            teamId = homeTeamId;
                        } else if (awayTeamPlayers.stream().anyMatch(player -> player.getId().equals(playerId))) {
                            teamId = awayTeamId;
                        }

                        if (teamId == null) {
                            continue; // Player not found in either team
                        }

                        Integer order = parseIntegerSafely(battingEntry.get("battingPosition"));
                        Integer runs = parseIntegerSafely(battingEntry.get("runs"));
                        Integer balls = parseIntegerSafely(battingEntry.get("balls"));
                        Integer dots = parseIntegerSafely(battingEntry.get("dots"));
                        Integer fours = parseIntegerSafely(battingEntry.get("fours"));
                        Integer sixes = parseIntegerSafely(battingEntry.get("sixes"));
                        Integer minutes = parseIntegerSafely(battingEntry.get("minutes"));
                        Double strikeRate = parseDoubleSafely(battingEntry.get("strikeRate"));
                        Boolean isOut = parseBooleanSafely(battingEntry.get("isOut"));

                        if (order != null && runs != null && balls != null && dots != null && fours != null &&
                            sixes != null && minutes != null && strikeRate != null && isOut != null) {

                            battingInnings.add(new BattingInningsData(
                                playerId, teamId, order, runs, balls, dots, fours, sixes, minutes, strikeRate, isOut
                            ));
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid player IDs
                    }
                }
            }
        }

        return battingInnings;
    }

    private List<BowlingInningsData> extractBowlingInnings(JsonNode cricketScorecardData, Integer homeTeamId, List<PlayerData> homeTeamPlayers, Integer awayTeamId, List<PlayerData> awayTeamPlayers) {
        List<BowlingInningsData> bowlingInnings = new ArrayList<>();

        JsonNode inningsNode = cricketScorecardData.get("innings");
        if (inningsNode == null || !inningsNode.isArray()) {
            return bowlingInnings;
        }

        // Create a map of all players for quick lookup
        var allPlayers = new ArrayList<PlayerData>();
        allPlayers.addAll(homeTeamPlayers);
        allPlayers.addAll(awayTeamPlayers);

        for (JsonNode inning : inningsNode) {
            JsonNode bowlingNode = inning.get("bowling");
            if (bowlingNode != null && bowlingNode.isArray()) {
                for (JsonNode bowlingEntry : bowlingNode) {
                    try {
                        JsonNode playerIdNode = bowlingEntry.get("playerId");
                        if (playerIdNode == null) {
                            continue;
                        }

                        Integer playerId = Integer.parseInt(playerIdNode.asText());

                        // Determine which team this player belongs to
                        Integer teamId = null;
                        if (homeTeamPlayers.stream().anyMatch(player -> player.getId().equals(playerId))) {
                            teamId = homeTeamId;
                        } else if (awayTeamPlayers.stream().anyMatch(player -> player.getId().equals(playerId))) {
                            teamId = awayTeamId;
                        }

                        if (teamId == null) {
                            continue; // Player not found in either team
                        }

                        Integer order = parseIntegerSafely(bowlingEntry.get("position"));
                        Double overs = parseDoubleSafely(bowlingEntry.get("overs"));
                        Integer maidens = parseIntegerSafely(bowlingEntry.get("maidens"));
                        Integer runs = parseIntegerSafely(bowlingEntry.get("runsConceded"));
                        Integer wickets = parseIntegerSafely(bowlingEntry.get("wickets"));
                        Integer dots = parseIntegerSafely(bowlingEntry.get("dots"));
                        Integer noBalls = parseIntegerSafely(bowlingEntry.get("noBalls"));
                        Integer wides = parseIntegerSafely(bowlingEntry.get("wides"));
                        Integer foursConceded = parseIntegerSafely(bowlingEntry.get("foursConceded"));
                        Integer sixesConceded = parseIntegerSafely(bowlingEntry.get("sixesConceded"));
                        Double economy = parseDoubleSafely(bowlingEntry.get("economyRate"));

                        // Calculate strike rate: balls/wickets (where wickets is 0 then this doesn't apply)
                        Double strikeRate = 0.0;
                        JsonNode ballsNode = bowlingEntry.get("balls");
                        if (ballsNode != null && wickets != null && wickets > 0) {
                            Integer balls = parseIntegerSafely(ballsNode);
                            if (balls != null) {
                                strikeRate = (double) balls / wickets;
                            }
                        }

                        if (order != null && overs != null && maidens != null && runs != null && wickets != null &&
                            dots != null && noBalls != null && wides != null && foursConceded != null &&
                            sixesConceded != null && economy != null) {

                            bowlingInnings.add(new BowlingInningsData(
                                playerId, teamId, order, overs, maidens, runs, wickets, dots, noBalls, wides,
                                foursConceded, sixesConceded, economy, strikeRate
                            ));
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid player IDs
                    }
                }
            }
        }

        return bowlingInnings;
    }

    private Integer parseIntegerSafely(JsonNode node) {
        if (node == null || node.isNull()) return null;
        try {
            return Integer.parseInt(node.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDoubleSafely(JsonNode node) {
        if (node == null || node.isNull()) return null;
        try {
            return Double.parseDouble(node.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBooleanSafely(JsonNode node) {
        if (node == null || node.isNull()) return null;
        return node.asBoolean();
    }

    public static class PlayerData {
        private final Integer id;
        private final String displayName;

        public PlayerData(Integer id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public Integer getId() { return id; }
        public String getDisplayName() { return displayName; }
    }

    public static class BattingInningsData {
        private final Integer playerId;
        private final Integer teamId;
        private final Integer order;
        private final Integer runs;
        private final Integer balls;
        private final Integer dots;
        private final Integer fours;
        private final Integer sixes;
        private final Integer minutes;
        private final Double strikeRate;
        private final Boolean isOut;

        public BattingInningsData(Integer playerId, Integer teamId, Integer order, Integer runs, Integer balls, Integer dots, Integer fours, Integer sixes, Integer minutes, Double strikeRate, Boolean isOut) {
            this.playerId = playerId;
            this.teamId = teamId;
            this.order = order;
            this.runs = runs;
            this.balls = balls;
            this.dots = dots;
            this.fours = fours;
            this.sixes = sixes;
            this.minutes = minutes;
            this.strikeRate = strikeRate;
            this.isOut = isOut;
        }

        public Integer getPlayerId() { return playerId; }
        public Integer getTeamId() { return teamId; }
        public Integer getOrder() { return order; }
        public Integer getRuns() { return runs; }
        public Integer getBalls() { return balls; }
        public Integer getDots() { return dots; }
        public Integer getFours() { return fours; }
        public Integer getSixes() { return sixes; }
        public Integer getMinutes() { return minutes; }
        public Double getStrikeRate() { return strikeRate; }
        public Boolean getIsOut() { return isOut; }
    }

    public static class BowlingInningsData {
        private final Integer playerId;
        private final Integer teamId;
        private final Integer order;
        private final Double overs;
        private final Integer maidens;
        private final Integer runs;
        private final Integer wickets;
        private final Integer dots;
        private final Integer noBalls;
        private final Integer wides;
        private final Integer foursConceded;
        private final Integer sixesConceded;
        private final Double economy;
        private final Double strikeRate;

        public BowlingInningsData(Integer playerId, Integer teamId, Integer order, Double overs, Integer maidens, Integer runs, Integer wickets,
                                 Integer dots, Integer noBalls, Integer wides, Integer foursConceded,
                                 Integer sixesConceded, Double economy, Double strikeRate) {
            this.playerId = playerId;
            this.teamId = teamId;
            this.order = order;
            this.overs = overs;
            this.maidens = maidens;
            this.runs = runs;
            this.wickets = wickets;
            this.dots = dots;
            this.noBalls = noBalls;
            this.wides = wides;
            this.foursConceded = foursConceded;
            this.sixesConceded = sixesConceded;
            this.economy = economy;
            this.strikeRate = strikeRate;
        }

        public Integer getPlayerId() { return playerId; }
        public Integer getTeamId() { return teamId; }
        public Integer getOrder() { return order; }
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
    }

    public static class ScorecardData {
        private final String homeTeam;
        private final String awayTeam;
        private final String result;
        private final String competitionName;
        private final String startDateTime;
        private final Integer homeTeamId;
        private final Integer awayTeamId;
        private final List<PlayerData> homeTeamPlayers;
        private final List<PlayerData> awayTeamPlayers;
        private final List<BattingInningsData> battingInnings;
        private final List<BowlingInningsData> bowlingInnings;

        public ScorecardData(String homeTeam, String awayTeam, String result, String competitionName, String startDateTime, Integer homeTeamId, Integer awayTeamId, List<PlayerData> homeTeamPlayers, List<PlayerData> awayTeamPlayers, List<BattingInningsData> battingInnings, List<BowlingInningsData> bowlingInnings) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.result = result;
            this.competitionName = competitionName;
            this.startDateTime = startDateTime;
            this.homeTeamId = homeTeamId;
            this.awayTeamId = awayTeamId;
            this.homeTeamPlayers = homeTeamPlayers != null ? homeTeamPlayers : new ArrayList<>();
            this.awayTeamPlayers = awayTeamPlayers != null ? awayTeamPlayers : new ArrayList<>();
            this.battingInnings = battingInnings != null ? battingInnings : new ArrayList<>();
            this.bowlingInnings = bowlingInnings != null ? bowlingInnings : new ArrayList<>();
        }

        public String getHomeTeam() { return homeTeam; }
        public String getAwayTeam() { return awayTeam; }
        public String getResult() { return result; }
        public String getCompetitionName() { return competitionName; }
        public String getStartDateTime() { return startDateTime; }
        public Integer getHomeTeamId() { return homeTeamId; }
        public Integer getAwayTeamId() { return awayTeamId; }
        public List<PlayerData> getHomeTeamPlayers() { return homeTeamPlayers; }
        public List<PlayerData> getAwayTeamPlayers() { return awayTeamPlayers; }
        public List<BattingInningsData> getBattingInnings() { return battingInnings; }
        public List<BowlingInningsData> getBowlingInnings() { return bowlingInnings; }
    }
}