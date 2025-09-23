package com.blackcat.cricketstats.infrastructure.persistence.battinginnings;

import com.blackcat.cricketstats.domain.battinginnings.BattingInnings;
import com.blackcat.cricketstats.domain.battinginnings.BattingInningsRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.blackcat.cricketstats.jooq.Tables.BATTING_INNINGS;

@Repository
public class JooqBattingInningsRepository implements BattingInningsRepository {

    private final DSLContext dsl;

    public JooqBattingInningsRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Integer save(BattingInnings battingInnings) {
        return dsl.transactionResult(configuration -> {
            var ctx = configuration.dsl();

            var record = ctx.insertInto(BATTING_INNINGS)
                    .set(BATTING_INNINGS.GAME, battingInnings.getGameId())
                    .set(BATTING_INNINGS.PLAYER, battingInnings.getPlayerId())
                    .set(BATTING_INNINGS.TEAM_ID, battingInnings.getTeamId())
                    .set(BATTING_INNINGS.INNINGS_ORDER, battingInnings.getInningsOrder())
                    .set(BATTING_INNINGS.RUNS, battingInnings.getRuns())
                    .set(BATTING_INNINGS.BALLS, battingInnings.getBalls())
                    .set(BATTING_INNINGS.DOTS, battingInnings.getDots())
                    .set(BATTING_INNINGS.FOURS_SCORED, battingInnings.getFoursScored())
                    .set(BATTING_INNINGS.SIXES_SCORED, battingInnings.getSixesScored())
                    .set(BATTING_INNINGS.MINUTES_BATTED, battingInnings.getMinutesBatted())
                    .set(BATTING_INNINGS.STRIKE_RATE, battingInnings.getStrikeRate())
                    .returning(BATTING_INNINGS.ID)
                    .fetchOne();

            return record != null ? record.getId() : null;
        });
    }
}