package com.blackcat.cricketstats.infrastructure.persistence.bowlinginnings;

import com.blackcat.cricketstats.domain.bowlinginnings.BowlingInnings;
import com.blackcat.cricketstats.domain.bowlinginnings.BowlingInningsRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.blackcat.cricketstats.jooq.Tables.BOWLING_INNINGS;

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
}