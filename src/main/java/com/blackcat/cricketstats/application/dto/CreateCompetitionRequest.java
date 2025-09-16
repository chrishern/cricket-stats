package com.blackcat.cricketstats.application.dto;

import com.blackcat.cricketstats.domain.competition.Format;
import com.blackcat.cricketstats.domain.competition.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCompetitionRequest {
    @NotNull(message = "Format is required")
    private Format format;

    @NotBlank(message = "Start year is required")
    private String startYear;

    @NotBlank(message = "End year is required")
    private String endYear;

    private Country country;

    @NotNull(message = "International flag is required")
    private Boolean international;

    @NotBlank(message = "Name is required")
    private String name;

    public Format getFormat() { return format; }
    public void setFormat(Format format) { this.format = format; }

    public String getStartYear() { return startYear; }
    public void setStartYear(String startYear) { this.startYear = startYear; }

    public String getEndYear() { return endYear; }
    public void setEndYear(String endYear) { this.endYear = endYear; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public Boolean getInternational() { return international; }
    public void setInternational(Boolean international) { this.international = international; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}