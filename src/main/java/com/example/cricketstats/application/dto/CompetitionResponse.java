package com.example.cricketstats.application.dto;

import com.example.cricketstats.domain.competition.Competition;
import com.example.cricketstats.domain.competition.Format;
import com.example.cricketstats.domain.competition.Country;

public class CompetitionResponse {
    private Integer id;
    private Format format;
    private String startYear;
    private String endYear;
    private Country country;
    private boolean international;
    private String name;

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
    public Format getFormat() { return format; }
    public String getStartYear() { return startYear; }
    public String getEndYear() { return endYear; }
    public Country getCountry() { return country; }
    public boolean isInternational() { return international; }
    public String getName() { return name; }
}