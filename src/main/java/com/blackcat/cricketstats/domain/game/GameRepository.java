package com.blackcat.cricketstats.domain.game;

import java.util.List;

public interface GameRepository {
    Integer save(Game game);
    List<GameWithTeamNames> findByCompetitionId(Integer competitionId);
}