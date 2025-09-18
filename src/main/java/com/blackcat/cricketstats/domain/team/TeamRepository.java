package com.blackcat.cricketstats.domain.team;

import java.util.Optional;

public interface TeamRepository {
    Integer save(Team team);
    Optional<Team> findByName(String name);
}