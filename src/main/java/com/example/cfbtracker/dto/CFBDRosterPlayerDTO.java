package com.example.cfbtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CFBDRosterPlayerDTO {
    @JsonProperty("id")
    private Integer id;
    // CFBD roster uses camelCase field names (e.g., firstName, lastName).
    // Map them correctly so names are not lost on ingest.
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    private String position;
    private String team;
    @JsonProperty("team_id")
    private Integer teamId;
    private String height;
    private Integer weight;
    private Integer jersey;
    private String year;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }
    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }
    public Integer getJersey() { return jersey; }
    public void setJersey(Integer jersey) { this.jersey = jersey; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
}
