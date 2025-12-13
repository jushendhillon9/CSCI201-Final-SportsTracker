package com.example.cfbtracker.services;

import com.example.cfbtracker.dto.TeamDTO;
import com.example.cfbtracker.dto.TeamListItem;
import com.example.cfbtracker.models.Team;
import com.example.cfbtracker.models.Game;
import com.example.cfbtracker.repositories.TeamRepository;
import com.example.cfbtracker.repositories.GameRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.example.cfbtracker.dto.CFBDRecordDTO;
import com.example.cfbtracker.dto.CFBDRankingDTO;
import com.example.cfbtracker.services.CFBDService;

@Service
public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;
    private final CFBDService cfbdService;

    public TeamService(TeamRepository teamRepository, GameRepository gameRepository, CFBDService cfbdService) {
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
        this.cfbdService = cfbdService;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public List<TeamListItem> getTeams(String conference, String search, boolean forceRefresh) {
        // only ingest when forced; otherwise serve from DB to avoid failures/slow calls
        if (forceRefresh) {
            Integer seasonCurrent = Year.now().getValue();
            try {
                ingestTeams(cfbdService.fetchTeams());
            } catch (Exception ex) {
                log.warn("Skipping team ingest due to error: {}", ex.getMessage());
            }
            try {
                ingestRecords(seasonCurrent);
            } catch (Exception ex) {
                log.warn("Skipping records ingest due to error: {}", ex.getMessage());
            }
            try {
                ingestRankings(seasonCurrent, null); // latest poll
            } catch (Exception ex) {
                log.warn("Skipping rankings ingest due to error: {}", ex.getMessage());
            }
        }

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
        CFBDRecordDTO[] records;
        try {
            records = cfbdService.fetchRecords(season);
        } catch (Exception ex) {
            log.error("Failed to fetch records from CFBD", ex);
            return 0;
        }
        if (records == null) {
            log.warn("CFBD records returned null for season {}", season);
            return 0;
        }
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
            CFBDRecordDTO.Totals totals = dto.getTotal() != null ? dto.getTotal() : dto.getOverall();
            if (totals != null) {
                Integer wins = totals.getWins();
                Integer losses = totals.getLosses();
                team.setWins(wins == null ? 0 : wins);
                team.setLosses(losses == null ? 0 : losses);
            }
            teamRepository.save(team);
            count++;
        }
        // Fallback: derive records from games if API data missing
        updateRecordsFromGamesIfMissing();
        // Fallback: per-team fetch when wins/losses are zero
        List<Team> zeroTeams = teamRepository.findAll().stream()
                .filter(t -> (t.getWins() == null || t.getWins() == 0) && (t.getLosses() == null || t.getLosses() == 0))
                .collect(Collectors.toList());
        for (Team t : zeroTeams) {
            try {
                // first try without classification to catch teams filtered out in bulk calls
                CFBDRecordDTO[] teamRecs = cfbdService.fetchRecords(season, t.getName(), false);
                if (teamRecs == null || teamRecs.length == 0) {
                    teamRecs = cfbdService.fetchRecords(season, t.getName(), true);
                }
                if (teamRecs != null && teamRecs.length > 0) {
                    CFBDRecordDTO rec = teamRecs[0];
                    CFBDRecordDTO.Totals totals = rec.getTotal() != null ? rec.getTotal() : rec.getOverall();
                    if (totals != null) {
                        Integer wins = totals.getWins();
                        Integer losses = totals.getLosses();
                        t.setWins(wins == null ? 0 : wins);
                        t.setLosses(losses == null ? 0 : losses);
                        teamRepository.save(t);
                        log.info("Updated team {} ({}) wins/losses from per-team fetch: {}/{}", t.getName(), t.getTeamid(), t.getWins(), t.getLosses());
                    }
                }
            } catch (Exception ex) {
                log.warn("Per-team record fetch failed for {}: {}", t.getName(), ex.getMessage());
            }
        }
        return count;
    }

    public int ingestRankings(Integer season, Integer week) {
        CFBDRankingDTO[] rankings = cfbdService.fetchRankings(season, week);
        if (rankings == null) return 0;
        // Reset all rankings before applying new poll so unranked teams stay null
        teamRepository.findAll().forEach(t -> {
            t.setRanking(null);
            teamRepository.save(t);
        });
        int count = 0;
        for (CFBDRankingDTO dto : rankings) {
            if (dto.getPolls() == null) continue;
            // pick first poll that has ranks (prefer CFP or AP)
            CFBDRankingDTO.Poll selected = dto.getPolls().stream()
                    .filter(p -> p.getRanks() != null && !p.getRanks().isEmpty())
                    .sorted((a, b) -> {
                        // prioritize AP Top 25 over others
                        boolean aAp = a.getPoll() != null && a.getPoll().toLowerCase().contains("ap");
                        boolean bAp = b.getPoll() != null && b.getPoll().toLowerCase().contains("ap");
                        if (aAp == bAp) return 0;
                        return aAp ? -1 : 1;
                    })
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

    private void updateRecordsFromGamesIfMissing() {
        List<Game> games = gameRepository.findAll();
        Map<Integer, int[]> recordMap = new HashMap<>();

        for (Game g : games) {
            if (g.getHomeTeam() == null || g.getAwayTeam() == null) continue;
            if (!"final".equalsIgnoreCase(g.getStatus())) continue;
            Integer homeId = g.getHomeTeam().getTeamid();
            Integer awayId = g.getAwayTeam().getTeamid();
            Integer homeScore = g.getHome_score();
            Integer awayScore = g.getAway_score();
            if (homeScore == null || awayScore == null) continue;

            recordMap.putIfAbsent(homeId, new int[]{0, 0});
            recordMap.putIfAbsent(awayId, new int[]{0, 0});

            if (homeScore > awayScore) {
                recordMap.get(homeId)[0] += 1;
                recordMap.get(awayId)[1] += 1;
            } else if (awayScore > homeScore) {
                recordMap.get(awayId)[0] += 1;
                recordMap.get(homeId)[1] += 1;
            }
        }

        if (recordMap.isEmpty()) return;

        for (Map.Entry<Integer, int[]> entry : recordMap.entrySet()) {
            Integer teamId = entry.getKey();
            int wins = entry.getValue()[0];
            int losses = entry.getValue()[1];
            Team team = teamRepository.findById(teamId).orElse(null);
            if (team == null) continue;
            boolean missing = team.getWins() == null && team.getLosses() == null;
            boolean zeroed = (team.getWins() == null || team.getWins() == 0) && wins > 0;
            if (missing || zeroed) {
                team.setWins(wins);
                team.setLosses(losses);
                teamRepository.save(team);
            }
        }
    }
}
