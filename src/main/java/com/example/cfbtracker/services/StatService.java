package com.example.cfbtracker.services;

import org.springframework.stereotype.Service;
import java.util.List;
import com.example.cfbtracker.models.Stat;
import com.example.cfbtracker.repositories.StatRepository;

@Service
public class StatService {

    private final StatRepository statRepository;

    public StatService(StatRepository statRepository) {
        this.statRepository = statRepository;
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

    public Stat saveStat(Stat stat) {
        return statRepository.save(stat);
    }
}
