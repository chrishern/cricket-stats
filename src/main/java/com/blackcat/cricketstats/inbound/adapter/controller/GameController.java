package com.blackcat.cricketstats.inbound.adapter.controller;

import com.blackcat.cricketstats.application.dto.CreateGameRequest;
import com.blackcat.cricketstats.application.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<Void> createGame(@Valid @RequestBody CreateGameRequest request) {
        Integer gameId = gameService.createGame(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/games/" + gameId));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}