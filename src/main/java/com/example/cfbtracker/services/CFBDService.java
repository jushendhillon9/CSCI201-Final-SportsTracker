package com.example.cfbtracker.services;

import com.example.cfbtracker.dto.TeamDTO;
import com.example.cfbtracker.dto.CFBDGameDTO;
import com.example.cfbtracker.dto.CFBDPlayerDTO;
import com.example.cfbtracker.dto.CFBDRecordDTO;
import com.example.cfbtracker.dto.CFBDRankingDTO;
import com.example.cfbtracker.dto.CFBDPlayerStatDTO;
import com.example.cfbtracker.dto.CFBDRosterPlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.StringJoiner;

@Service
public class CFBDService {

    private static final Logger log = LoggerFactory.getLogger(CFBDService.class);

    private final RestTemplate restTemplate;

    @Value("${cfbd.api.base:https://api.collegefootballdata.com}")
    private String baseUrl;

    @Value("${cfbd.api.key:}")
    private String apiKey;

    public CFBDService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TeamDTO[] fetchTeams() {
        String url = baseUrl + "/teams";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        } else {
            log.warn("CFBD API key is missing; request may fail with 401");
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<TeamDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    TeamDTO[].class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("Failed to fetch teams from CFBD", ex);
            return new TeamDTO[0];
        }
    }

    public CFBDGameDTO[] fetchGames(Integer season, Integer week, String status, String teamName) {
        StringJoiner params = new StringJoiner("&", "?", "");
        if (season != null) params.add("year=" + season);
        if (week != null) params.add("week=" + week);
        if (status != null && !status.isBlank()) params.add("status=" + status);
        if (teamName != null && !teamName.isBlank()) params.add("team=" + teamName);
        String url = baseUrl + "/games" + (params.length() > 1 ? params.toString() : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        } else {
            log.warn("CFBD API key is missing; request may fail with 401");
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<CFBDGameDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CFBDGameDTO[].class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("Failed to fetch games from CFBD", ex);
            return new CFBDGameDTO[0];
        }
    }

    public CFBDPlayerDTO[] fetchPlayers(Integer season, String teamName, String search) {
        String url;
        if (search != null && !search.isBlank()) {
            url = baseUrl + "/player/search?searchTerm=" + search;
        } else {
            StringJoiner params = new StringJoiner("&", "?", "");
            if (season != null) params.add("year=" + season);
            if (teamName != null && !teamName.isBlank()) params.add("team=" + teamName);
            url = baseUrl + "/players" + (params.length() > 1 ? params.toString() : "");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        } else {
            log.warn("CFBD API key is missing; request may fail with 401");
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<CFBDPlayerDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CFBDPlayerDTO[].class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("Failed to fetch players from CFBD", ex);
            return new CFBDPlayerDTO[0];
        }
    }

    public CFBDRecordDTO[] fetchRecords(Integer season) {
        String url = baseUrl + "/records" + (season != null ? "?year=" + season : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        } else {
            log.warn("CFBD API key is missing; request may fail with 401");
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<CFBDRecordDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CFBDRecordDTO[].class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("Failed to fetch records from CFBD", ex);
            return new CFBDRecordDTO[0];
        }
    }

    public CFBDRankingDTO[] fetchRankings(Integer season, Integer week) {
        StringJoiner params = new StringJoiner("&", "?", "");
        if (season != null) params.add("year=" + season);
        if (week != null) params.add("week=" + week);
        String url = baseUrl + "/rankings" + (params.length() > 1 ? params.toString() : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        } else {
            log.warn("CFBD API key is missing; request may fail with 401");
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<CFBDRankingDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CFBDRankingDTO[].class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("Failed to fetch rankings from CFBD", ex);
            return new CFBDRankingDTO[0];
        }
    }

    public CFBDPlayerStatDTO[] fetchPlayerStats(Integer season, String teamName, Integer playerId) {
        StringJoiner params = new StringJoiner("&", "?", "");
        if (season != null) params.add("year=" + season);
        if (teamName != null && !teamName.isBlank()) params.add("team=" + teamName);
        if (playerId != null) params.add("playerId=" + playerId);
        String url = baseUrl + "/stats/player" + (params.length() > 1 ? params.toString() : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        } else {
            log.warn("CFBD API key is missing; request may fail with 401");
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<CFBDPlayerStatDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CFBDPlayerStatDTO[].class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("Failed to fetch player stats from CFBD", ex);
            return new CFBDPlayerStatDTO[0];
        }
    }

    public CFBDRosterPlayerDTO[] fetchRoster(Integer season, String teamName) {
        StringJoiner params = new StringJoiner("&", "?", "");
        if (season != null) params.add("year=" + season);
        if (teamName != null && !teamName.isBlank()) params.add("team=" + teamName);
        String url = baseUrl + "/roster" + (params.length() > 1 ? params.toString() : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (apiKey != null && !apiKey.isBlank()) {
            headers.setBearerAuth(apiKey);
        } else {
            log.warn("CFBD API key is missing; request may fail with 401");
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<CFBDRosterPlayerDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CFBDRosterPlayerDTO[].class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            log.error("Failed to fetch roster from CFBD", ex);
            return new CFBDRosterPlayerDTO[0];
        }
    }
}
