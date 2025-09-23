package com.blackcat.cricketstats.application.service;

import com.blackcat.cricketstats.application.dto.CreateGameRequest;
import com.blackcat.cricketstats.domain.battinginnings.BattingInnings;
import com.blackcat.cricketstats.domain.battinginnings.BattingInningsRepository;
import com.blackcat.cricketstats.domain.bowlinginnings.BowlingInnings;
import com.blackcat.cricketstats.domain.bowlinginnings.BowlingInningsRepository;
import com.blackcat.cricketstats.domain.competition.Competition;
import com.blackcat.cricketstats.domain.competition.CompetitionRepository;
import com.blackcat.cricketstats.domain.competition.Country;
import com.blackcat.cricketstats.domain.competition.Format;
import com.blackcat.cricketstats.domain.game.Game;
import com.blackcat.cricketstats.domain.game.GameRepository;
import com.blackcat.cricketstats.domain.game.GameWithTeamNames;
import com.blackcat.cricketstats.domain.player.Player;
import com.blackcat.cricketstats.domain.player.PlayerRepository;
import com.blackcat.cricketstats.domain.team.Team;
import com.blackcat.cricketstats.domain.team.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final CompetitionRepository competitionRepository;
    private final PlayerRepository playerRepository;
    private final BattingInningsRepository battingInningsRepository;
    private final BowlingInningsRepository bowlingInningsRepository;
    private final ScorecardScrapingService scorecardScrapingService;

    public GameService(GameRepository gameRepository, TeamRepository teamRepository,
                      CompetitionRepository competitionRepository, PlayerRepository playerRepository,
                      BattingInningsRepository battingInningsRepository, BowlingInningsRepository bowlingInningsRepository,
                      ScorecardScrapingService scorecardScrapingService) {
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.competitionRepository = competitionRepository;
        this.playerRepository = playerRepository;
        this.battingInningsRepository = battingInningsRepository;
        this.bowlingInningsRepository = bowlingInningsRepository;
        this.scorecardScrapingService = scorecardScrapingService;
    }

    public Integer createGame(CreateGameRequest request) {
        ScorecardScrapingService.ScorecardData scorecardData =
            scorecardScrapingService.scrapeScorecard(request.getScorecardUrl());

        Integer homeTeamId = getOrCreateTeam(scorecardData.getHomeTeamId(), scorecardData.getHomeTeam());
        Integer awayTeamId = getOrCreateTeam(scorecardData.getAwayTeamId(), scorecardData.getAwayTeam());
        Integer competitionId = getOrCreateCompetition(scorecardData.getCompetitionName());

        savePlayers(scorecardData.getHomeTeamPlayers());
        savePlayers(scorecardData.getAwayTeamPlayers());

        LocalDateTime startDateTime = parseStartDateTime(scorecardData.getStartDateTime());

        Game game = new Game(
                null,
                competitionId,
                homeTeamId,
                awayTeamId,
                scorecardData.getResult(),
                startDateTime
        );

        Integer gameId = gameRepository.save(game);

        saveBattingInnings(gameId, scorecardData.getBattingInnings());
        saveBowlingInnings(gameId, scorecardData.getBowlingInnings());

        return gameId;
    }

    public List<GameWithTeamNames> getGamesByCompetitionId(Integer competitionId) {
        return gameRepository.findByCompetitionId(competitionId);
    }

    private Integer getOrCreateTeam(Integer teamId, String teamName) {
        if (teamId == null) {
            throw new IllegalArgumentException("Team ID is required but was not found in scorecard data");
        }

        return teamRepository.findById(teamId)
                .map(Team::getId)
                .orElseGet(() -> {
                    Team newTeam = new Team(teamId, "England", false, teamName);
                    return teamRepository.save(newTeam);
                });
    }

    private Integer getOrCreateCompetition(String competitionName) {
        if (competitionName == null || competitionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Competition name is required but was not found in scorecard data");
        }

        return competitionRepository.findByName(competitionName)
                .map(Competition::getId)
                .orElseGet(() -> {
                    // Create a new competition with default values
                    // You may want to adjust these defaults based on your requirements
                    Competition newCompetition = new Competition(
                            null,
                            Format.T_20, // Default format - you might want to make this configurable
                            "2024", // Default start year - you might want to extract this from the data
                            "2024", // Default end year - you might want to extract this from the data
                            Country.ENGLAND, // Default country - you might want to make this configurable
                            false, // Default to non-international
                            competitionName
                    );
                    return competitionRepository.save(newCompetition);
                });
    }

    private LocalDateTime parseStartDateTime(String startDateTimeString) {
        if (startDateTimeString == null || startDateTimeString.trim().isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return LocalDateTime.parse(startDateTimeString, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    private void savePlayers(List<ScorecardScrapingService.PlayerData> playerDataList) {
        for (ScorecardScrapingService.PlayerData playerData : playerDataList) {
            getOrCreatePlayer(playerData.getId(), playerData.getDisplayName());
        }
    }

    private Integer getOrCreatePlayer(Integer playerId, String fullName) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID is required but was not found in scorecard data");
        }

        return playerRepository.findById(playerId)
                .map(Player::getId)
                .orElseGet(() -> {
                    Player newPlayer = new Player(playerId, fullName);
                    return playerRepository.save(newPlayer);
                });
    }

    private void saveBattingInnings(Integer gameId, List<ScorecardScrapingService.BattingInningsData> battingInningsDataList) {
        for (ScorecardScrapingService.BattingInningsData battingInningsData : battingInningsDataList) {
            BattingInnings battingInnings = new BattingInnings(
                null, // id will be auto-generated
                gameId,
                battingInningsData.getPlayerId(),
                battingInningsData.getRuns(),
                battingInningsData.getBalls(),
                battingInningsData.getDots(),
                battingInningsData.getFours(),
                battingInningsData.getSixes(),
                battingInningsData.getMinutes(),
                battingInningsData.getStrikeRate()
            );

            battingInningsRepository.save(battingInnings);
        }
    }

    private void saveBowlingInnings(Integer gameId, List<ScorecardScrapingService.BowlingInningsData> bowlingInningsDataList) {
        for (ScorecardScrapingService.BowlingInningsData bowlingInningsData : bowlingInningsDataList) {
            BowlingInnings bowlingInnings = new BowlingInnings(
                null, // id will be auto-generated
                gameId,
                bowlingInningsData.getPlayerId(),
                bowlingInningsData.getOvers(),
                bowlingInningsData.getMaidens(),
                bowlingInningsData.getRuns(),
                bowlingInningsData.getWickets(),
                bowlingInningsData.getDots(),
                bowlingInningsData.getNoBalls(),
                bowlingInningsData.getWides(),
                bowlingInningsData.getFoursConceded(),
                bowlingInningsData.getSixesConceded(),
                bowlingInningsData.getEconomy(),
                bowlingInningsData.getStrikeRate()
            );

            bowlingInningsRepository.save(bowlingInnings);
        }
    }
}