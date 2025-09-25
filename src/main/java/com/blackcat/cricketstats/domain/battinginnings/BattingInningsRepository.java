package com.blackcat.cricketstats.domain.battinginnings;

import com.blackcat.cricketstats.application.dto.BattingInningsResponse;

import java.util.List;

public interface BattingInningsRepository {
    Integer save(BattingInnings battingInnings);
    List<BattingInningsResponse> findWithPlayerNamesByGameIdAndTeamId(Integer gameId, Integer teamId);
}