package com.blackcat.cricketstats.infrastructure.persistence.bowlinginnings;

import com.blackcat.cricketstats.application.dto.BowlingInningsResponse;
import com.blackcat.cricketstats.domain.bowlinginnings.BowlingInnings;
import com.blackcat.cricketstats.domain.bowlinginnings.BowlingInningsRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.blackcat.cricketstats.jooq.Tables.BOWLING_INNINGS;
import static com.blackcat.cricketstats.jooq.Tables.PLAYER;

@Repository
public class JooqBowlingInningsRepository implements BowlingInningsRepository {

    private final DSLContext dsl;

    public JooqBowlingInningsRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Integer save(BowlingInnings bowlingInnings) {
        return dsl.transactionResult(configuration -> {
            var ctx = configuration.dsl();

            var record = ctx.insertInto(BOWLING_INNINGS)
                    .set(BOWLING_INNINGS.GAME, bowlingInnings.getGameId())
                    .set(BOWLING_INNINGS.PLAYER, bowlingInnings.getPlayerId())
                    .set(BOWLING_INNINGS.TEAM_ID, bowlingInnings.getTeamId())
                    .set(BOWLING_INNINGS.INNINGS_ORDER, bowlingInnings.getInningsOrder())
                    .set(BOWLING_INNINGS.OVERS, bowlingInnings.getOvers())
                    .set(BOWLING_INNINGS.MAIDENS, bowlingInnings.getMaidens())
                    .set(BOWLING_INNINGS.RUNS, bowlingInnings.getRuns())
                    .set(BOWLING_INNINGS.WICKETS, bowlingInnings.getWickets())
                    .set(BOWLING_INNINGS.DOTS, bowlingInnings.getDots())
                    .set(BOWLING_INNINGS.NO_BALLS, bowlingInnings.getNoBalls())
                    .set(BOWLING_INNINGS.WIDES, bowlingInnings.getWides())
                    .set(BOWLING_INNINGS.FOURS_CONCEDED, bowlingInnings.getFoursConceded())
                    .set(BOWLING_INNINGS.SIXES_CONCEDED, bowlingInnings.getSixesConceded())
                    .set(BOWLING_INNINGS.ECONOMY, bowlingInnings.getEconomy())
                    .set(BOWLING_INNINGS.STRIKE_RATE, bowlingInnings.getStrikeRate())
                    .returning(BOWLING_INNINGS.ID)
                    .fetchOne();

            return record != null ? record.getId() : null;
        });
    }

    @Override
    public List<BowlingInningsResponse> findWithPlayerNamesByGameIdAndTeamId(Integer gameId, Integer teamId) {
        return dsl.select(
                        BOWLING_INNINGS.ID,
                        BOWLING_INNINGS.GAME,
                        BOWLING_INNINGS.PLAYER,
                        PLAYER.FULL_NAME,
                        BOWLING_INNINGS.TEAM_ID,
                        BOWLING_INNINGS.INNINGS_ORDER,
                        BOWLING_INNINGS.OVERS,
                        BOWLING_INNINGS.MAIDENS,
                        BOWLING_INNINGS.RUNS,
                        BOWLING_INNINGS.WICKETS,
                        BOWLING_INNINGS.DOTS,
                        BOWLING_INNINGS.NO_BALLS,
                        BOWLING_INNINGS.WIDES,
                        BOWLING_INNINGS.FOURS_CONCEDED,
                        BOWLING_INNINGS.SIXES_CONCEDED,
                        BOWLING_INNINGS.ECONOMY,
                        BOWLING_INNINGS.STRIKE_RATE
                )
                .from(BOWLING_INNINGS)
                .join(PLAYER).on(BOWLING_INNINGS.PLAYER.eq(PLAYER.ID))
                .where(BOWLING_INNINGS.GAME.eq(gameId))
                .and(BOWLING_INNINGS.TEAM_ID.eq(teamId))
                .orderBy(BOWLING_INNINGS.INNINGS_ORDER)
                .fetch(record -> new BowlingInningsResponse(
                        record.get(BOWLING_INNINGS.ID),
                        record.get(BOWLING_INNINGS.GAME),
                        record.get(BOWLING_INNINGS.PLAYER),
                        record.get(PLAYER.FULL_NAME),
                        record.get(BOWLING_INNINGS.TEAM_ID),
                        record.get(BOWLING_INNINGS.INNINGS_ORDER),
                        record.get(BOWLING_INNINGS.OVERS),
                        record.get(BOWLING_INNINGS.MAIDENS),
                        record.get(BOWLING_INNINGS.RUNS),
                        record.get(BOWLING_INNINGS.WICKETS),
                        record.get(BOWLING_INNINGS.DOTS),
                        record.get(BOWLING_INNINGS.NO_BALLS),
                        record.get(BOWLING_INNINGS.WIDES),
                        record.get(BOWLING_INNINGS.FOURS_CONCEDED),
                        record.get(BOWLING_INNINGS.SIXES_CONCEDED),
                        record.get(BOWLING_INNINGS.ECONOMY),
                        record.get(BOWLING_INNINGS.STRIKE_RATE)
                ));
    }
}