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
import java.util.ArrayList;

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

    public List<PlayerListItem> getPlayers(Integer teamId, String position, String search, boolean forceRefresh, Integer season, Integer limit, String sort) {
        // simple refresh before serve; replace with last-updated check in production
        Integer seasonCurrent = season != null ? season : Year.now().getValue();
        String teamName = null;
        if (teamId != null) {
            teamName = teamRepository.findById(teamId).map(Team::getName).orElse(null);
        }

        // Avoid long fetches on every request; only ingest if forced or no players at all
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
        } else if (limit != null && sort != null && sort.equalsIgnoreCase("tds")) {
            // Fast path: top players by TDs from stats table
            List<Object[]> top = statRepository.findTopPlayersByTds(seasonCurrent, "td");
            List<Integer> ids = new ArrayList<>();
            for (Object[] row : top) {
                if (ids.size() >= limit) break;
                Integer pid = (Integer) row[0];
                if (pid != null) ids.add(pid);
            }
            players = ids.isEmpty() ? playerRepository.findAll() : playerRepository.findAllById(ids);
        } else {
            players = playerRepository.findAll();
        }

        final Integer statSeason = seasonCurrent;
        List<PlayerListItem> dtoList = players.stream().map(p -> toDto(p, statSeason)).collect(Collectors.toList());

        if (sort != null && sort.equalsIgnoreCase("tds")) {
            dtoList = dtoList.stream()
                    .sorted((a, b) -> Integer.compare(
                            b.getTotalTDs() == null ? 0 : b.getTotalTDs(),
                            a.getTotalTDs() == null ? 0 : a.getTotalTDs()))
                    .collect(Collectors.toList());
        }

        if (limit != null && limit > 0 && dtoList.size() > limit) {
            dtoList = dtoList.subList(0, limit);
        }

        return dtoList;
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

            // Persist raw stat row (allow game null for season aggregates), but only keep TDs / yards / games to save space
            String typeLabel = cat;
            if (s.getStatType() != null && !s.getStatType().isBlank()) {
                typeLabel = (cat + "_" + s.getStatType()).toLowerCase();
            }
            boolean keep = typeLabel.contains("td") ||
                           typeLabel.contains("yard") ||
                           typeLabel.contains("yd") ||
                           typeLabel.contains("game") ||
                           typeLabel.contains("gp");
            if (keep) {
                Stat stat = new Stat();
                stat.setSeason(s.getSeason());
                stat.setStat_type(typeLabel);
                stat.setValue(s.getStat());
                stat.setPlayer(player);
                if (s.getGameId() != null) {
                    stat.setGame(gameRepository.findById(s.getGameId()).orElse(null));
                }
                statRepository.save(stat);
            }
            count++;
        }
        return count;
    }

    private PlayerListItem toDto(Player player, Integer season) {
        PlayerListItem dto = new PlayerListItem();
        dto.setPlayerId(player.getPlayerid());
        dto.setName(player.getName());
        if (player.getTeam() != null) {
            dto.setTeamId(player.getTeam().getTeamid());
            dto.setTeamName(player.getTeam().getName());
        }
        dto.setPosition(player.getPosition());
        dto.setYear(player.getYear());
        dto.setJerseyNumber(player.getJersey_number());
        int pass = player.getPassing_yards() == null ? 0 : player.getPassing_yards();
        int rush = player.getRushing_yards() == null ? 0 : player.getRushing_yards();
        int recv = player.getReceiving_yards() == null ? 0 : player.getReceiving_yards();
        int tds = 0;
        int gamesPlayed = 0;
        List<Stat> stats = List.of();
        if (season != null) {
            stats = statRepository.findByPlayerPlayeridAndSeason(player.getPlayerid(), season);
        } else {
            stats = statRepository.findByPlayerPlayerid(player.getPlayerid());
        }
        if (stats != null && !stats.isEmpty()) {
            pass = stats.stream()
                    .filter(s -> s.getStat_type() != null && s.getStat_type().contains("pass") && s.getStat_type().contains("yard"))
                    .mapToInt(s -> s.getValue() == null ? 0 : s.getValue().intValue())
                    .sum();
            rush = stats.stream()
                    .filter(s -> s.getStat_type() != null && s.getStat_type().contains("rush") && s.getStat_type().contains("yard"))
                    .mapToInt(s -> s.getValue() == null ? 0 : s.getValue().intValue())
                    .sum();
            recv = stats.stream()
                    .filter(s -> s.getStat_type() != null && s.getStat_type().contains("recv") && s.getStat_type().contains("yard"))
                    .mapToInt(s -> s.getValue() == null ? 0 : s.getValue().intValue())
                    .sum();
            tds = stats.stream()
                    .filter(s -> s.getStat_type() != null && s.getStat_type().contains("td"))
                    .mapToInt(s -> s.getValue() == null ? 0 : s.getValue().intValue())
                    .sum();
            gamesPlayed = stats.stream()
                    .filter(s -> s.getStat_type() != null && (s.getStat_type().contains("game") || s.getStat_type().contains("gp")))
                    .mapToInt(s -> s.getValue() == null ? 0 : s.getValue().intValue())
                    .sum();
            // Fallback: if no explicit games stat, count distinct non-null game ids
            if (gamesPlayed == 0) {
                gamesPlayed = (int) stats.stream()
                        .filter(s -> s.getGame() != null && s.getGame().getGameid() != null)
                        .map(s -> s.getGame().getGameid())
                        .distinct()
                        .count();
            }
        }
        dto.setPassingYards(pass);
        dto.setRushingYards(rush);
        dto.setReceivingYards(recv);
        dto.setTotalTDs(tds);
        dto.setGamesPlayed(gamesPlayed);
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
