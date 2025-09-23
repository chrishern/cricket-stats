package com.blackcat.cricketstats.inbound.adapter.controller;

import com.blackcat.cricketstats.application.dto.CreateCompetitionRequest;
import com.blackcat.cricketstats.application.dto.CompetitionResponse;
import com.blackcat.cricketstats.application.service.CompetitionService;
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

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
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
}