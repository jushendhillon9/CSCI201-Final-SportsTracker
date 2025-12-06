package com.example.cfbtracker.repositories;

import com.example.cfbtracker.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Integer> {
    List<Game> findBySeason(Integer season);
    List<Game> findBySeasonAndWeek(Integer season, Integer week);
    List<Game> findByStatus(String status);

    @Query("SELECT g FROM Game g WHERE (:status IS NULL OR g.status = :status) AND (:season IS NULL OR g.season = :season) AND (:week IS NULL OR g.week = :week) AND (:teamId IS NULL OR g.homeTeam.teamid = :teamId OR g.awayTeam.teamid = :teamId)")
    List<Game> searchGames(@Param("status") String status, @Param("season") Integer season, @Param("week") Integer week, @Param("teamId") Integer teamId);
}
