package com.example.cfbtracker.dto;

public class TeamDTO {
    private Integer id;
    private String school;
    private String conference;
    private String[] logos;

    public TeamDTO() {
        // default constructor for deserialization
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getConference() { return conference; }
    public void setConference(String conference) { this.conference = conference; }

    public String[] getLogos() { return logos; }
    public void setLogos(String[] logos) { this.logos = logos; }
}
