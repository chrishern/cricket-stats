package com.blackcat.cricketstats.application.service;

import com.blackcat.cricketstats.application.dto.CreateGameRequest;
import com.blackcat.cricketstats.domain.competition.Competition;
import com.blackcat.cricketstats.domain.competition.CompetitionRepository;
import com.blackcat.cricketstats.domain.competition.Country;
import com.blackcat.cricketstats.domain.competition.Format;
import com.blackcat.cricketstats.domain.game.Game;
import com.blackcat.cricketstats.domain.game.GameRepository;
import com.blackcat.cricketstats.domain.team.Team;
import com.blackcat.cricketstats.domain.team.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final CompetitionRepository competitionRepository;
    private final ScorecardScrapingService scorecardScrapingService;

    public GameService(GameRepository gameRepository, TeamRepository teamRepository,
                      CompetitionRepository competitionRepository, ScorecardScrapingService scorecardScrapingService) {
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.competitionRepository = competitionRepository;
        this.scorecardScrapingService = scorecardScrapingService;
    }

    public Integer createGame(CreateGameRequest request) {
        ScorecardScrapingService.ScorecardData scorecardData =
            scorecardScrapingService.scrapeScorecard(request.getScorecardUrl());

        Integer homeTeamId = getOrCreateTeam(scorecardData.getHomeTeamId(), scorecardData.getHomeTeam());
        Integer awayTeamId = getOrCreateTeam(scorecardData.getAwayTeamId(), scorecardData.getAwayTeam());
        Integer competitionId = getOrCreateCompetition(scorecardData.getCompetitionName());

        LocalDateTime startDateTime = parseStartDateTime(scorecardData.getStartDateTime());

        Game game = new Game(
                null,
                competitionId,
                homeTeamId,
                awayTeamId,
                scorecardData.getResult(),
                startDateTime
        );

        return gameRepository.save(game);
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
}