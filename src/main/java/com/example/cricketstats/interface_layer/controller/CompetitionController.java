package com.example.cricketstats.interface_layer.controller;

import com.example.cricketstats.application.dto.CreateCompetitionRequest;
import com.example.cricketstats.application.dto.CompetitionResponse;
import com.example.cricketstats.application.service.CompetitionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {
    
    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @PostMapping
    public ResponseEntity<CompetitionResponse> createCompetition(@Valid @RequestBody CreateCompetitionRequest request) {
        try {
            CompetitionResponse response = competitionService.createCompetition(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}