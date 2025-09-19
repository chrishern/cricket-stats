package com.blackcat.cricketstats.domain.competition;

import java.util.Optional;

public interface CompetitionRepository {
    Integer save(Competition competition);
    Optional<Competition> findByName(String name);
}