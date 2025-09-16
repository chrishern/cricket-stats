package com.blackcat.cricketstats.application.dto;

import com.blackcat.cricketstats.domain.competition.Competition;
import com.blackcat.cricketstats.domain.competition.Format;
import com.blackcat.cricketstats.domain.competition.Country;

public class CompetitionResponse {
    private Integer id;
    private Format format;
    private String startYear;
    private String endYear;
    private Country country;
    private boolean international;
    private String name;

    public CompetitionResponse() {
    }

    public CompetitionResponse(Competition competition) {
        this.id = competition.getId();
        this.format = competition.getFormat();
        this.startYear = competition.getStartYear();
        this.endYear = competition.getEndYear();
        this.country = competition.getCountry();
        this.international = competition.isInternational();
        this.name = competition.getName();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Format getFormat() { return format; }
    public void setFormat(Format format) { this.format = format; }

    public String getStartYear() { return startYear; }
    public void setStartYear(String startYear) { this.startYear = startYear; }

    public String getEndYear() { return endYear; }
    public void setEndYear(String endYear) { this.endYear = endYear; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public boolean isInternational() { return international; }
    public void setInternational(boolean international) { this.international = international; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}