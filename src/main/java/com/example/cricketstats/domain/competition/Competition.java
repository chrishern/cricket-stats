package com.example.cricketstats.domain.competition;

import java.util.Objects;

public class Competition {
    private Integer id;
    private Format format;
    private String startYear;
    private String endYear;
    private Country country;
    private boolean international;
    private String name;

    public Competition(Integer id, Format format, String startYear, String endYear, 
                      Country country, boolean international, String name) {
        this.id = id;
        this.format = Objects.requireNonNull(format, "Format cannot be null");
        this.startYear = Objects.requireNonNull(startYear, "Start year cannot be null");
        this.endYear = Objects.requireNonNull(endYear, "End year cannot be null");
        this.international = international;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        
        if (!international && country == null) {
            throw new IllegalArgumentException("Country is required when international is false");
        }
        this.country = country;
        
        validateYears(startYear, endYear);
    }

    private void validateYears(String startYear, String endYear) {
        try {
            int start = Integer.parseInt(startYear);
            int end = Integer.parseInt(endYear);
            if (start < 1800 || start > 3000 || end < 1800 || end > 3000) {
                throw new IllegalArgumentException("Years must be between 1800 and 3000");
            }
            if (start > end) {
                throw new IllegalArgumentException("Start year cannot be after end year");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Years must be valid numbers");
        }
    }

    public Integer getId() { return id; }
    public Format getFormat() { return format; }
    public String getStartYear() { return startYear; }
    public String getEndYear() { return endYear; }
    public Country getCountry() { return country; }
    public boolean isInternational() { return international; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competition that = (Competition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}