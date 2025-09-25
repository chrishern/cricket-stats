package com.blackcat.cricketstats.infrastructure.persistence.battinginnings;

import com.blackcat.cricketstats.application.dto.BattingInningsResponse;
import com.blackcat.cricketstats.domain.battinginnings.BattingInnings;
import com.blackcat.cricketstats.domain.battinginnings.BattingInningsRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.blackcat.cricketstats.jooq.Tables.BATTING_INNINGS;
import static com.blackcat.cricketstats.jooq.Tables.PLAYER;

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

    @Override
    public List<BattingInningsResponse> findWithPlayerNamesByGameIdAndTeamId(Integer gameId, Integer teamId) {
        return dsl.select(
                        BATTING_INNINGS.ID,
                        BATTING_INNINGS.GAME,
                        BATTING_INNINGS.PLAYER,
                        PLAYER.FULL_NAME,
                        BATTING_INNINGS.TEAM_ID,
                        BATTING_INNINGS.INNINGS_ORDER,
                        BATTING_INNINGS.RUNS,
                        BATTING_INNINGS.BALLS,
                        BATTING_INNINGS.DOTS,
                        BATTING_INNINGS.FOURS_SCORED,
                        BATTING_INNINGS.SIXES_SCORED,
                        BATTING_INNINGS.MINUTES_BATTED,
                        BATTING_INNINGS.STRIKE_RATE
                )
                .from(BATTING_INNINGS)
                .join(PLAYER).on(BATTING_INNINGS.PLAYER.eq(PLAYER.ID))
                .where(BATTING_INNINGS.GAME.eq(gameId))
                .and(BATTING_INNINGS.TEAM_ID.eq(teamId))
                .orderBy(BATTING_INNINGS.INNINGS_ORDER)
                .fetch(record -> new BattingInningsResponse(
                        record.get(BATTING_INNINGS.ID),
                        record.get(BATTING_INNINGS.GAME),
                        record.get(BATTING_INNINGS.PLAYER),
                        record.get(PLAYER.FULL_NAME),
                        record.get(BATTING_INNINGS.TEAM_ID),
                        record.get(BATTING_INNINGS.INNINGS_ORDER),
                        record.get(BATTING_INNINGS.RUNS),
                        record.get(BATTING_INNINGS.BALLS),
                        record.get(BATTING_INNINGS.DOTS),
                        record.get(BATTING_INNINGS.FOURS_SCORED),
                        record.get(BATTING_INNINGS.SIXES_SCORED),
                        record.get(BATTING_INNINGS.MINUTES_BATTED),
                        record.get(BATTING_INNINGS.STRIKE_RATE)
                ));
    }
}