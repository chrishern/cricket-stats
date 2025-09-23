package com.blackcat.cricketstats.infrastructure.persistence.competition;

import com.blackcat.cricketstats.domain.competition.Competition;
import com.blackcat.cricketstats.domain.competition.CompetitionRepository;
import com.blackcat.cricketstats.domain.competition.Country;
import com.blackcat.cricketstats.domain.competition.Format;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.blackcat.cricketstats.jooq.Tables.COMPETITION;

@Repository
public class JooqCompetitionRepository implements CompetitionRepository {

    private final DSLContext dsl;

    public JooqCompetitionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Integer save(Competition competition) {
        return dsl.transactionResult(configuration -> {
            var ctx = configuration.dsl();

            Integer competitionId = ctx.insertInto(COMPETITION)
                    .set(COMPETITION.FORMAT, competition.getFormat().name())
                    .set(COMPETITION.START_YEAR, competition.getStartYear())
                    .set(COMPETITION.END_YEAR, competition.getEndYear())
                    .set(COMPETITION.COUNTRY, competition.getCountry() != null ? competition.getCountry().name() : null)
                    .set(COMPETITION.INTERNATIONAL, competition.isInternational())
                    .set(COMPETITION.NAME, competition.getName())
                    .returning(COMPETITION.ID)
                    .fetchOne(COMPETITION.ID);

            if (competitionId == null) {
                throw new RuntimeException("Failed to insert competition");
            }

            return competitionId;
        });
    }

    @Override
    public Optional<Competition> findByName(String name) {
        Record record = dsl.select()
                .from(COMPETITION)
                .where(COMPETITION.NAME.eq(name))
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        Competition competition = new Competition(
                record.get(COMPETITION.ID),
                Format.valueOf(record.get(COMPETITION.FORMAT)),
                record.get(COMPETITION.START_YEAR),
                record.get(COMPETITION.END_YEAR),
                record.get(COMPETITION.COUNTRY) != null ? Country.valueOf(record.get(COMPETITION.COUNTRY)) : null,
                record.get(COMPETITION.INTERNATIONAL),
                record.get(COMPETITION.NAME)
        );

        return Optional.of(competition);
    }

    @Override
    public List<Competition> findAll() {
        return dsl.select()
                .from(COMPETITION)
                .orderBy(COMPETITION.START_YEAR, COMPETITION.NAME)
                .fetch(record -> new Competition(
                        record.get(COMPETITION.ID),
                        Format.valueOf(record.get(COMPETITION.FORMAT)),
                        record.get(COMPETITION.START_YEAR),
                        record.get(COMPETITION.END_YEAR),
                        record.get(COMPETITION.COUNTRY) != null ? Country.valueOf(record.get(COMPETITION.COUNTRY)) : null,
                        record.get(COMPETITION.INTERNATIONAL),
                        record.get(COMPETITION.NAME)
                ));
    }

}