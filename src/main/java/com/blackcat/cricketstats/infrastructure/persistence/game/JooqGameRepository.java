package com.blackcat.cricketstats.infrastructure.persistence.game;

import com.blackcat.cricketstats.domain.game.Game;
import com.blackcat.cricketstats.domain.game.GameRepository;
import com.blackcat.cricketstats.domain.game.GameWithTeamNames;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static com.blackcat.cricketstats.jooq.Tables.GAME;
import static com.blackcat.cricketstats.jooq.Tables.TEAM;

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

            var insertQuery = ctx.insertInto(GAME)
                    .set(GAME.COMPETITION, game.getCompetitionId())
                    .set(GAME.HOME_TEAM, game.getHomeTeamId())
                    .set(GAME.AWAY_TEAM, game.getAwayTeamId())
                    .set(GAME.RESULT, game.getResult());

            if (game.getStartDateTime() != null) {
                insertQuery.set(org.jooq.impl.DSL.field("start_date_time"),
                              Timestamp.valueOf(game.getStartDateTime()));
            }

            Integer gameId = insertQuery
                    .returning(GAME.ID)
                    .fetchOne(GAME.ID);

            if (gameId == null) {
                throw new RuntimeException("Failed to insert game");
            }

            return gameId;
        });
    }

    @Override
    public List<GameWithTeamNames> findByCompetitionId(Integer competitionId) {
        var homeTeam = TEAM.as("home_team");
        var awayTeam = TEAM.as("away_team");

        return dsl.select(
                GAME.ID,
                GAME.COMPETITION,
                GAME.HOME_TEAM,
                homeTeam.NAME,
                GAME.AWAY_TEAM,
                awayTeam.NAME,
                GAME.RESULT,
                org.jooq.impl.DSL.field("start_date_time")
            )
            .from(GAME)
            .innerJoin(homeTeam).on(GAME.HOME_TEAM.eq(homeTeam.ID))
            .innerJoin(awayTeam).on(GAME.AWAY_TEAM.eq(awayTeam.ID))
            .where(GAME.COMPETITION.eq(competitionId))
            .orderBy(
                org.jooq.impl.DSL.field("start_date_time").asc().nullsLast(),
                homeTeam.NAME.asc()
            )
            .fetch(record -> new GameWithTeamNames(
                record.get(GAME.ID),
                record.get(GAME.COMPETITION),
                record.get(GAME.HOME_TEAM),
                record.get(homeTeam.NAME),
                record.get(GAME.AWAY_TEAM),
                record.get(awayTeam.NAME),
                record.get(GAME.RESULT),
                record.get(org.jooq.impl.DSL.field("start_date_time")) != null ?
                    ((Timestamp) record.get(org.jooq.impl.DSL.field("start_date_time"))).toLocalDateTime() : null
            ));
    }
}