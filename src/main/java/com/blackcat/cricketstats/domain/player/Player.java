package com.blackcat.cricketstats.domain.player;

import java.util.Objects;

public class Player {
    private Integer id;
    private String fullName;

    public Player(Integer id, String fullName) {
        this.id = id;
        this.fullName = Objects.requireNonNull(fullName, "Full name cannot be null");

        if (fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
    }

    public Integer getId() { return id; }
    public String getFullName() { return fullName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}