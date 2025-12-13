package com.example.cfbtracker.services;

import com.example.cfbtracker.models.Stat;
import com.example.cfbtracker.models.Team;
import com.example.cfbtracker.repositories.StatRepository;
import com.example.cfbtracker.repositories.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
public class StatService {

    private static final Logger log = LoggerFactory.getLogger(StatService.class);

    private final StatRepository statRepository;
    private final TeamRepository teamRepository;
    private final PlayerService playerService;
    private final CFBDService cfbdService;

    public StatService(StatRepository statRepository, TeamRepository teamRepository, PlayerService playerService, CFBDService cfbdService) {
        this.statRepository = statRepository;
        this.teamRepository = teamRepository;
        this.playerService = playerService;
        this.cfbdService = cfbdService;
    }

    public List<Stat> getAllStats() {
        return statRepository.findAll();
    }

    public List<Stat> getStats(Integer season, Integer teamId, Integer playerId, Integer gameId) {
        return statRepository.findAll().stream()
                .filter(s -> season == null || season.equals(s.getSeason()))
                .filter(s -> playerId == null || (s.getPlayer() != null && playerId.equals(s.getPlayer().getPlayerid())))
                .filter(s -> gameId == null || (s.getGame() != null && gameId.equals(s.getGame().getGameid())))
                .filter(s -> teamId == null || (s.getPlayer() != null
                        && s.getPlayer().getTeam() != null
                        && teamId.equals(s.getPlayer().getTeam().getTeamid())))
                .toList();
    }

    /**
    * Fetch player stats from CFBD for all teams in DB and ingest into the stats table.
    * @param season optional season (defaults to current year)
    * @param force if true, clears existing stats for the season before ingesting
    * @return number of stat rows ingested
    */
    public int ingestSeasonStats(Integer season, boolean force, Integer teamId) {
        int targetSeason = (season != null) ? season : Year.now().getValue();
        if (force) {
            statRepository.deleteBySeason(targetSeason);
        }
        int total = 0;
        List<Team> teams;
        if (teamId != null) {
            teams = teamRepository.findById(teamId).map(List::of).orElse(List.of());
        } else {
            teams = teamRepository.findAll();
        }
        for (Team team : teams) {
            String teamName = team.getName();
            if (teamName == null || teamName.isBlank()) {
                continue;
            }
            try {
                // Use season aggregates endpoint; default to regular season
                total += playerService.ingestPlayerStats(
                        cfbdService.fetchPlayerSeasonStats(targetSeason, teamName, "regular")
                );
            } catch (Exception ex) {
                log.error("Failed to ingest stats for team {} ({}) season {}", teamName, team.getTeamid(), targetSeason, ex);
            }
        }
        return total;
    }

    public Stat saveStat(Stat stat) {
        return statRepository.save(stat);
    }
}
