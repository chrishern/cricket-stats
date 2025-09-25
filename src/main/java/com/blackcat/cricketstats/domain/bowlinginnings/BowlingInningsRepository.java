package com.blackcat.cricketstats.domain.bowlinginnings;

import com.blackcat.cricketstats.application.dto.BowlingInningsResponse;

import java.util.List;

public interface BowlingInningsRepository {
    Integer save(BowlingInnings bowlingInnings);
    List<BowlingInningsResponse> findWithPlayerNamesByGameIdAndTeamId(Integer gameId, Integer teamId);
}