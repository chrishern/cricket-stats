package com.blackcat.cricketstats.infrastructure.persistence.game;

import com.blackcat.cricketstats.domain.game.Game;
import com.blackcat.cricketstats.domain.game.GameRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.blackcat.cricketstats.jooq.Tables.GAME;

@Repository
public class JooqGameRepository implements GameRepository {

    private final DSLContext dsl;

    public JooqGameRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Integer save(Game game) {
        return dsl.transactionResult(configuration -> {
            var ctx = configuration.dsl();

            Integer gameId = ctx.insertInto(GAME)
                    .set(GAME.COMPETITION, game.getCompetitionId())
                    .set(GAME.HOME_TEAM, game.getHomeTeamId())
                    .set(GAME.AWAY_TEAM, game.getAwayTeamId())
                    .set(GAME.RESULT, game.getResult())
                    .returning(GAME.ID)
                    .fetchOne(GAME.ID);

            if (gameId == null) {
                throw new RuntimeException("Failed to insert game");
            }

            return gameId;
        });
    }
}