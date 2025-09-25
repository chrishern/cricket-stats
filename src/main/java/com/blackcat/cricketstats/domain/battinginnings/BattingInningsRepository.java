package com.blackcat.cricketstats.domain.battinginnings;

import java.util.List;

public interface BattingInningsRepository {
    Integer save(BattingInnings battingInnings);
    List<BattingInnings> findByGameIdAndTeamId(Integer gameId, Integer teamId);
}