package com.blackcat.cricketstats.inbound.adapter.controller;

import com.blackcat.cricketstats.application.dto.CreateCompetitionRequest;
import com.blackcat.cricketstats.application.dto.CompetitionResponse;
import com.blackcat.cricketstats.application.dto.GameResponse;
import com.blackcat.cricketstats.application.service.CompetitionService;
import com.blackcat.cricketstats.application.service.GameService;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final GameService gameService;

    public CompetitionController(CompetitionService competitionService, GameService gameService) {
        this.competitionService = competitionService;
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<Void> createCompetition(@Valid @RequestBody CreateCompetitionRequest request) {
        Integer competitionId = competitionService.createCompetition(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/competitions/" + competitionId));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CompetitionResponse>> getAllCompetitions() {
        List<CompetitionResponse> competitions = competitionService.getAllCompetitions()
                .stream()
                .map(CompetitionResponse::new)
                .toList();

        return ResponseEntity.ok(competitions);
    }

    @GetMapping("/{competitionId}/games")
    public ResponseEntity<List<GameResponse>> getGamesByCompetitionId(@PathVariable Integer competitionId) {
        List<GameResponse> games = gameService.getGamesByCompetitionId(competitionId)
                .stream()
                .map(game -> new GameResponse(
                        game.getId(),
                        game.getHomeTeamName(),
                        game.getAwayTeamName(),
                        game.getStartDateTime(),
                        game.getResult()
                ))
                .toList();

        return ResponseEntity.ok(games);
    }
}