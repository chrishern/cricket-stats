package com.blackcat.cricketstats.application.service;

import com.blackcat.cricketstats.application.dto.CreateGameRequest;
import com.blackcat.cricketstats.domain.game.Game;
import com.blackcat.cricketstats.domain.game.GameRepository;
import com.blackcat.cricketstats.domain.team.Team;
import com.blackcat.cricketstats.domain.team.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final ScorecardScrapingService scorecardScrapingService;

    public GameService(GameRepository gameRepository, TeamRepository teamRepository,
                      ScorecardScrapingService scorecardScrapingService) {
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.scorecardScrapingService = scorecardScrapingService;
    }

    public Integer createGame(CreateGameRequest request) {
        ScorecardScrapingService.ScorecardData scorecardData =
            scorecardScrapingService.scrapeScorecard(request.getScorecardUrl());

        Integer homeTeamId = getOrCreateTeam(scorecardData.getHomeTeam());
        Integer awayTeamId = getOrCreateTeam(scorecardData.getAwayTeam());

        Game game = new Game(
                null,
                1,
                homeTeamId,
                awayTeamId,
                scorecardData.getResult()
        );

        return gameRepository.save(game);
    }

    private Integer getOrCreateTeam(String teamName) {
        return teamRepository.findByName(teamName)
                .map(Team::getId)
                .orElseGet(() -> {
                    Team newTeam = new Team(null, "England", false, teamName);
                    return teamRepository.save(newTeam);
                });
    }
}