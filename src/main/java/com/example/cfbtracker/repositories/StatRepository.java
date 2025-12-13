package com.example.cfbtracker.repositories;

import com.example.cfbtracker.models.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Integer> {
    List<Stat> findBySeason(Integer season);
    void deleteBySeason(Integer season);
    List<Stat> findByPlayerPlayeridAndSeason(Integer playerId, Integer season);
    List<Stat> findByPlayerPlayerid(Integer playerId);

    @Query("select s.player.playerid as playerId, sum(s.value) as totalTd " +
           "from Stat s " +
           "where (:season is null or s.season = :season) and lower(s.stat_type) like %:tdToken% " +
           "group by s.player.playerid " +
           "order by totalTd desc")
    List<Object[]> findTopPlayersByTds(@Param("season") Integer season, @Param("tdToken") String tdToken);
}
