package com.blackcat.cricketstats.domain.team;

import java.util.Objects;

public class Team {
    private Integer id;
    private String country;
    private boolean international;
    private String name;

    public Team(Integer id, String country, boolean international, String name) {
        this.id = id;
        this.country = Objects.requireNonNull(country, "Country cannot be null");
        this.international = international;
        this.name = Objects.requireNonNull(name, "Name cannot be null");

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    public Integer getId() { return id; }
    public String getCountry() { return country; }
    public boolean isInternational() { return international; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}