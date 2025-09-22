package com.blackcat.cricketstats.infrastructure.persistence.player;

import com.blackcat.cricketstats.domain.player.Player;
import com.blackcat.cricketstats.domain.player.PlayerRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.blackcat.cricketstats.jooq.Tables.PLAYER;

@Repository
public class JooqPlayerRepository implements PlayerRepository {

    private final DSLContext dsl;

    public JooqPlayerRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Integer save(Player player) {
        return dsl.transactionResult(configuration -> {
            var ctx = configuration.dsl();

            ctx.insertInto(PLAYER)
                    .set(PLAYER.ID, player.getId())
                    .set(PLAYER.FULL_NAME, player.getFullName())
                    .execute();

            return player.getId();
        });
    }

    @Override
    public Optional<Player> findById(Integer id) {
        return dsl.selectFrom(PLAYER)
                .where(PLAYER.ID.eq(id))
                .fetchOptional()
                .map(record -> new Player(
                        record.getId(),
                        record.getFullName()
                ));
    }
}