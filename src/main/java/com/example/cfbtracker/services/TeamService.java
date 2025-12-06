package com.example.cfbtracker.services;

import com.example.cfbtracker.dto.TeamDTO;
import com.example.cfbtracker.dto.TeamListItem;
import com.example.cfbtracker.models.Team;
import com.example.cfbtracker.repositories.TeamRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import com.example.cfbtracker.dto.CFBDRecordDTO;
import com.example.cfbtracker.dto.CFBDRankingDTO;
import com.example.cfbtracker.services.CFBDService;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final CFBDService cfbdService;

    public TeamService(TeamRepository teamRepository, CFBDService cfbdService) {
        this.teamRepository = teamRepository;
        this.cfbdService = cfbdService;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public List<TeamListItem> getTeams(String conference, String search) {
        // simple refresh before serve; replace with last-updated check in production
        ingestTeams(cfbdService.fetchTeams());
        ingestRecords(null); // let CFBD decide latest if not provided
        ingestRankings(null, null); // latest poll

        List<Team> teams;
        if (conference != null && !conference.isBlank()) {
            teams = teamRepository.findByConferenceIgnoreCase(conference);
        } else if (search != null && !search.isBlank()) {
            teams = teamRepository.findByNameContainingIgnoreCase(search);
        } else {
            teams = teamRepository.findAll();
        }

        return teams.stream().map(this::toListItem).collect(Collectors.toList());
    }

    private TeamListItem toListItem(Team team) {
        TeamListItem item = new TeamListItem();
        item.setTeamId(team.getTeamid());
        item.setName(team.getName());
        item.setConference(team.getConference());
        item.setWins(team.getWins() == null ? 0 : team.getWins());
        item.setLosses(team.getLosses() == null ? 0 : team.getLosses());
        item.setRanking(team.getRanking());
        item.setLogoUrl(team.getLogo_url());
        return item;
    }

    public int ingestTeams(TeamDTO[] apiTeams) {
        if (apiTeams == null) return 0;
    
        int count = 0;
    
        for (TeamDTO dto : apiTeams) {
            Team team = teamRepository.findById(dto.getId()).orElse(new Team());
            team.setTeamid(dto.getId());
            team.setName(dto.getSchool());
            team.setConference(dto.getConference());
    
            if (dto.getLogos() != null && dto.getLogos().length > 0) {
                team.setLogo_url(dto.getLogos()[0]);
            }
    
            teamRepository.save(team);
            count++;
        }
    
        return count;
    }

    public int ingestRecords(Integer season) {
        CFBDRecordDTO[] records = cfbdService.fetchRecords(season);
        if (records == null) return 0;
        int count = 0;
        for (CFBDRecordDTO dto : records) {
            Integer id = dto.getTeamId();
            Team team = null;
            if (id != null) {
                team = teamRepository.findById(id).orElse(null);
            }
            if (team == null && dto.getTeam() != null) {
                team = teamRepository.findByNameIgnoreCase(dto.getTeam()).orElse(null);
            }
            if (team == null) {
                team = new Team();
                if (id != null) team.setTeamid(id);
                team.setName(dto.getTeam());
            }
            if (team.getTeamid() == null && id != null) {
                team.setTeamid(id);
            }
            if (dto.getConference() != null) {
                team.setConference(dto.getConference());
            }
            if (dto.getTotal() != null) {
                team.setWins(dto.getTotal().getWins());
                team.setLosses(dto.getTotal().getLosses());
            }
            teamRepository.save(team);
            count++;
        }
        return count;
    }

    public int ingestRankings(Integer season, Integer week) {
        CFBDRankingDTO[] rankings = cfbdService.fetchRankings(season, week);
        if (rankings == null) return 0;
        int count = 0;
        for (CFBDRankingDTO dto : rankings) {
            if (dto.getPolls() == null) continue;
            // pick first poll that has ranks (prefer CFP or AP)
            CFBDRankingDTO.Poll selected = dto.getPolls().stream()
                    .filter(p -> p.getRanks() != null && !p.getRanks().isEmpty())
                    .findFirst()
                    .orElse(null);
            if (selected == null) continue;
            for (CFBDRankingDTO.RankEntry entry : selected.getRanks()) {
                Integer id = entry.getTeamId();
                Team team = null;
                if (id != null) {
                    team = teamRepository.findById(id).orElse(null);
                }
                if (team == null && entry.getSchool() != null) {
                    team = teamRepository.findByNameIgnoreCase(entry.getSchool()).orElse(null);
                }
                if (team == null && entry.getTeam() != null) {
                    team = teamRepository.findByNameIgnoreCase(entry.getTeam()).orElse(null);
                }
                if (team == null) {
                    team = new Team();
                    if (id != null) team.setTeamid(id);
                    team.setName(entry.getSchool() != null ? entry.getSchool() : entry.getTeam());
                }
                if (team.getTeamid() == null && id != null) {
                    team.setTeamid(id);
                }
                team.setRanking(entry.getRank());
                teamRepository.save(team);
                count++;
            }
        }
        return count;
    }
     
}
