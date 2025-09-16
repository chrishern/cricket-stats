package com.blackcat.cricketstats.infrastructure.persistence.competition;

import com.blackcat.cricketstats.domain.competition.Competition;
import com.blackcat.cricketstats.domain.competition.CompetitionRepository;
import com.blackcat.cricketstats.jooq.tables.records.CompetitionRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.blackcat.cricketstats.jooq.Tables.COMPETITION;

@Repository
public class JooqCompetitionRepository implements CompetitionRepository {

    private final DSLContext dsl;

    public JooqCompetitionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Competition save(Competition competition) {
        return dsl.transactionResult(configuration -> {
            var ctx = configuration.dsl();

            CompetitionRecord record = ctx.insertInto(COMPETITION)
                    .set(COMPETITION.FORMAT, competition.getFormat().name())
                    .set(COMPETITION.START_YEAR, competition.getStartYear())
                    .set(COMPETITION.END_YEAR, competition.getEndYear())
                    .set(COMPETITION.COUNTRY, competition.getCountry() != null ? competition.getCountry().name() : null)
                    .set(COMPETITION.INTERNATIONAL, competition.isInternational())
                    .set(COMPETITION.NAME, competition.getName())
                    .returning(COMPETITION.ID)
                    .fetchOne();

            if (record == null) {
                throw new RuntimeException("Failed to insert competition");
            }

            return new Competition(
                    record.getId(),
                    competition.getFormat(),
                    competition.getStartYear(),
                    competition.getEndYear(),
                    competition.getCountry(),
                    competition.isInternational(),
                    competition.getName()
            );
        });
    }

}