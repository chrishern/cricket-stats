package com.blackcat.cricketstats.interface_layer.controller;

import com.blackcat.cricketstats.application.dto.CreateCompetitionRequest;
import com.blackcat.cricketstats.application.dto.ErrorResponse;
import com.blackcat.cricketstats.application.service.CompetitionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;
import java.net.URI;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @PostMapping
    public ResponseEntity<?> createCompetition(@Valid @RequestBody CreateCompetitionRequest request) {
        try {
            Integer competitionId = competitionService.createCompetition(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/api/competitions/" + competitionId));

            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        String errorMessage = fieldError.getDefaultMessage();
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}