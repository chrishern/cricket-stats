package com.example.cricketstats.infrastructure.persistence.competition;

import com.example.cricketstats.domain.competition.Competition;
import com.example.cricketstats.domain.competition.CompetitionRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.*;

@Repository
public class JooqCompetitionRepository implements CompetitionRepository {
    
    private final DSLContext dsl;

    public JooqCompetitionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Competition save(Competition competition) {
        Integer id = dsl.insertInto(table("competition"))
                .set(field("format"), competition.getFormat().name())
                .set(field("start_year"), competition.getStartYear())
                .set(field("end_year"), competition.getEndYear())
                .set(field("country"), competition.getCountry() != null ? competition.getCountry().name() : null)
                .set(field("international"), competition.isInternational())
                .set(field("name"), competition.getName())
                .returningResult(field("id", Integer.class))
                .fetchOne()
                .value1();

        return new Competition(
                id,
                competition.getFormat(),
                competition.getStartYear(),
                competition.getEndYear(),
                competition.getCountry(),
                competition.isInternational(),
                competition.getName()
        );
    }

}