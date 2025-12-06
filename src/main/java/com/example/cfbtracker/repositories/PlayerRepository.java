package com.example.cfbtracker.repositories;

import com.example.cfbtracker.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    List<Player> findByTeamTeamid(Integer teamId);
    List<Player> findByPositionIgnoreCase(String position);
    List<Player> findByNameContainingIgnoreCase(String name);
    List<Player> findByTeamTeamidAndPositionIgnoreCase(Integer teamId, String position);
}
