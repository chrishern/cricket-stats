package com.blackcat.cricketstats.domain.player;

import java.util.Optional;

public interface PlayerRepository {
    Integer save(Player player);
    Optional<Player> findById(Integer id);
}