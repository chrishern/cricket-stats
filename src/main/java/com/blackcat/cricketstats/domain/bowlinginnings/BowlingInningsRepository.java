package com.blackcat.cricketstats.domain.bowlinginnings;

import java.util.List;

public interface BowlingInningsRepository {
    Integer save(BowlingInnings bowlingInnings);
    List<BowlingInnings> findByGameIdAndTeamId(Integer gameId, Integer teamId);
}