package com.blackcat.cricketstats.application.service;

import com.blackcat.cricketstats.domain.competition.Competition;
import com.blackcat.cricketstats.domain.competition.CompetitionRepository;
import com.blackcat.cricketstats.application.dto.CreateCompetitionRequest;
import com.blackcat.cricketstats.application.dto.CompetitionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    public CompetitionResponse createCompetition(CreateCompetitionRequest request) {
        Competition competition = new Competition(
                null,
                request.getFormat(),
                request.getStartYear(),
                request.getEndYear(),
                request.getCountry(),
                request.getInternational(),
                request.getName()
        );

        Competition savedCompetition = competitionRepository.save(competition);
        return new CompetitionResponse(savedCompetition);
    }
}