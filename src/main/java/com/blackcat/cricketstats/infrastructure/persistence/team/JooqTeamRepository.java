package com.blackcat.cricketstats.infrastructure.persistence.team;

import com.blackcat.cricketstats.domain.team.Team;
import com.blackcat.cricketstats.domain.team.TeamRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.blackcat.cricketstats.jooq.Tables.TEAM;

@Repository
public class JooqTeamRepository implements TeamRepository {

    private final DSLContext dsl;

    public JooqTeamRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Integer save(Team team) {
        return dsl.transactionResult(configuration -> {
            var ctx = configuration.dsl();

            ctx.insertInto(TEAM)
                    .set(TEAM.ID, team.getId())
                    .set(TEAM.COUNTRY, team.getCountry())
                    .set(TEAM.INTERNATIONAL, team.isInternational())
                    .set(TEAM.NAME, team.getName())
                    .execute();

            return team.getId();
        });
    }

    @Override
    public Optional<Team> findByName(String name) {
        return dsl.selectFrom(TEAM)
                .where(TEAM.NAME.eq(name))
                .fetchOptional()
                .map(record -> new Team(
                        record.getId(),
                        record.getCountry(),
                        record.getInternational(),
                        record.getName()
                ));
    }

    @Override
    public Optional<Team> findById(Integer id) {
        return dsl.selectFrom(TEAM)
                .where(TEAM.ID.eq(id))
                .fetchOptional()
                .map(record -> new Team(
                        record.getId(),
                        record.getCountry(),
                        record.getInternational(),
                        record.getName()
                ));
    }
}