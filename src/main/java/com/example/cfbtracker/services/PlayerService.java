package com.example.cfbtracker.services;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import com.example.cfbtracker.models.Player;
import com.example.cfbtracker.models.Team;
import com.example.cfbtracker.models.Game;
import com.example.cfbtracker.models.Stat;
import com.example.cfbtracker.repositories.PlayerRepository;
import com.example.cfbtracker.repositories.TeamRepository;
import com.example.cfbtracker.repositories.GameRepository;
import com.example.cfbtracker.repositories.StatRepository;
import com.example.cfbtracker.dto.PlayerListItem;
import com.example.cfbtracker.dto.CFBDPlayerDTO;
import com.example.cfbtracker.dto.CFBDPlayerStatDTO;
import com.example.cfbtracker.dto.CFBDRosterPlayerDTO;
import java.util.stream.Collectors;

import com.example.cfbtracker.services.CFBDService;
import java.time.Year;

@Service
public class PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;
    private final StatRepository statRepository;
    private final CFBDService cfbdService;

    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository, GameRepository gameRepository, StatRepository statRepository, CFBDService cfbdService) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
        this.statRepository = statRepository;
        this.cfbdService = cfbdService;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public List<PlayerListItem> getPlayers(Integer teamId, String position, String search, boolean forceRefresh) {
        // simple refresh before serve; replace with last-updated check in production
        Integer seasonCurrent = Year.now().getValue();
        String teamName = null;
        if (teamId != null) {
            teamName = teamRepository.findById(teamId).map(Team::getName).orElse(null);
        }

        // Avoid long fetches on every request; only ingest if forced or empty
        long existingCount = playerRepository.count();
        int ingested = 0;
        if (forceRefresh || existingCount == 0) {
            ingested = ingestRostersForAllTeams(seasonCurrent);

            // Backup: use player search endpoints if rosters are empty
            CFBDPlayerDTO[] playersForTeam = cfbdService.fetchPlayers(seasonCurrent, teamName, search);
            if (playersForTeam != null && playersForTeam.length > 0) {
                ingested += ingestPlayers(playersForTeam);
            }
            CFBDPlayerDTO[] allPlayers = cfbdService.fetchPlayers(seasonCurrent, null, search);
            if (allPlayers != null && allPlayers.length > 0) {
                ingested += ingestPlayers(allPlayers);
            }
            if (ingested == 0 && existingCount == 0) {
                log.warn("CFBD players ingest returned zero rows for season {}", seasonCurrent);
            }

            ingestPlayerStats(cfbdService.fetchPlayerStats(seasonCurrent, teamName, null));
            ensureNamesPresent();
        }

        List<Player> players;
        if (teamId != null && position != null && !position.isBlank()) {
            players = playerRepository.findByTeamTeamidAndPositionIgnoreCase(teamId, position);
        } else if (teamId != null) {
            players = playerRepository.findByTeamTeamid(teamId);
        } else if (position != null && !position.isBlank()) {
            players = playerRepository.findByPositionIgnoreCase(position);
        } else if (search != null && !search.isBlank()) {
            players = playerRepository.findByNameContainingIgnoreCase(search);
        } else {
            players = playerRepository.findAll();
        }

        return players.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    public int ingestPlayers(CFBDPlayerDTO[] apiPlayers) {
        if (apiPlayers == null) return 0;
        int count = 0;
        for (CFBDPlayerDTO dto : apiPlayers) {
            if (dto.getId() == null) continue;
            // do not skip; ingest everything returned for broader coverage
            Player player = playerRepository.findById(dto.getId()).orElse(new Player());
            player.setPlayerid(dto.getId());
            String fullName = ((dto.getFirstName() != null ? dto.getFirstName() : "") + " " + (dto.getLastName() != null ? dto.getLastName() : "")).trim();
            if (fullName.isBlank()) {
                fullName = "Player " + dto.getId();
            }
            player.setName(fullName);
            player.setPosition(dto.getPosition());
            player.setYear(dto.getYear());

            Team team = null;
            if (dto.getTeamId() != null) {
                team = teamRepository.findById(dto.getTeamId()).orElseGet(() -> {
                    Team t = new Team();
                    t.setTeamid(dto.getTeamId());
                    t.setName(dto.getTeam());
                    return teamRepository.save(t);
                });
            }
            player.setTeam(team);
            playerRepository.save(player);
            count++;
        }
        return count;
    }

    public int ingestRosterPlayers(CFBDRosterPlayerDTO[] roster) {
        if (roster == null) return 0;
        int count = 0;
        for (CFBDRosterPlayerDTO dto : roster) {
            if (dto.getId() == null) continue;
            Player player = playerRepository.findById(dto.getId()).orElse(new Player());
            player.setPlayerid(dto.getId());
            String fullName = ((dto.getFirstName() != null ? dto.getFirstName() : "") + " " + (dto.getLastName() != null ? dto.getLastName() : "")).trim();
            if (fullName.isBlank()) {
                fullName = "Player " + dto.getId();
            }
            player.setName(fullName);
            player.setPosition(dto.getPosition());
            player.setYear(dto.getYear());
            player.setJersey_number(dto.getJersey());

            Team team = null;
            if (dto.getTeamId() != null) {
                team = teamRepository.findById(dto.getTeamId()).orElseGet(() -> {
                    Team t = new Team();
                    t.setTeamid(dto.getTeamId());
                    t.setName(dto.getTeam());
                    return teamRepository.save(t);
                });
            } else if (dto.getTeam() != null) {
                team = teamRepository.findByName(dto.getTeam()).orElse(null);
            }
            player.setTeam(team);
            playerRepository.save(player);
            count++;
        }
        return count;
    }

    private int ingestRostersForAllTeams(Integer season) {
        if (season == null) return 0;
        int total = 0;
        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            String teamName = team.getName();
            if (teamName == null || teamName.isBlank()) {
                log.warn("Skipping roster fetch because team name is null for team id={}", team.getTeamid());
                continue;
            }
            try {
                CFBDRosterPlayerDTO[] roster = cfbdService.fetchRoster(season, teamName);
                total += ingestRosterPlayers(roster);
            } catch (Exception ex) {
                log.error("Failed to fetch/ingest roster for team {} (id={}) season {}", teamName, team.getTeamid(), season, ex);
            }
        }
        return total;
    }

    private void ensureNamesPresent() {
        List<Player> missing = playerRepository.findAll().stream()
                .filter(p -> p.getName() == null || p.getName().isBlank())
                .toList();
        for (Player p : missing) {
            p.setName("Player " + p.getPlayerid());
            playerRepository.save(p);
        }
    }

    public int ingestPlayerStats(CFBDPlayerStatDTO[] stats) {
        if (stats == null) return 0;
        int count = 0;
        for (CFBDPlayerStatDTO s : stats) {
            if (s.getId() == null || s.getCategory() == null || s.getStat() == null) continue;
            Player player = playerRepository.findById(s.getId()).orElse(null);
            if (player == null) continue;
            String cat = s.getCategory().toLowerCase();
            int val = s.getStat().intValue();
            if (cat.contains("pass") && cat.contains("yard")) {
                player.setPassing_yards(val);
            } else if (cat.contains("rush") && cat.contains("yard")) {
                player.setRushing_yards(val);
            } else if (cat.contains("recv") && cat.contains("yard")) {
                player.setReceiving_yards(val);
            }
            playerRepository.save(player);

            // Persist raw stat row
            Stat stat = new Stat();
            stat.setSeason(s.getSeason());
            String typeLabel = cat;
            if (s.getStatType() != null && !s.getStatType().isBlank()) {
                typeLabel = (cat + "_" + s.getStatType()).toLowerCase();
            }
            stat.setStat_type(typeLabel);
            stat.setValue(s.getStat());
            stat.setPlayer(player);
            if (s.getGameId() != null) {
                gameRepository.findById(s.getGameId()).ifPresent(stat::setGame);
            }
            statRepository.save(stat);
            count++;
        }
        return count;
    }

    private PlayerListItem toDto(Player player) {
        PlayerListItem dto = new PlayerListItem();
        dto.setPlayerId(player.getPlayerid());
        dto.setName(player.getName());
        if (player.getTeam() != null) {
            dto.setTeamId(player.getTeam().getTeamid());
            dto.setTeamName(player.getTeam().getName());
        }
        dto.setPosition(player.getPosition());
        dto.setYear(player.getYear());
        dto.setPassingYards(player.getPassing_yards() == null ? 0 : player.getPassing_yards());
        dto.setRushingYards(player.getRushing_yards() == null ? 0 : player.getRushing_yards());
        dto.setReceivingYards(player.getReceiving_yards() == null ? 0 : player.getReceiving_yards());
        return dto;
    }

    /**
     * Convenience helper to fetch and ingest player stats for an optional team/player/season.
     */
    public int fetchAndIngestPlayerStats(Integer season, Integer teamId, Integer playerId) {
        String teamName = null;
        if (teamId != null) {
            teamName = teamRepository.findById(teamId).map(Team::getName).orElse(null);
        }
        CFBDPlayerStatDTO[] stats = cfbdService.fetchPlayerStats(season, teamName, playerId);
        return ingestPlayerStats(stats);
    }
}
