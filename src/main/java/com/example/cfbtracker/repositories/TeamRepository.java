package com.example.cfbtracker.repositories;

import com.example.cfbtracker.models.Team;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    Optional<Team> findByName(String name);
    Optional<Team> findByNameIgnoreCase(String name);
    List<Team> findByConferenceIgnoreCase(String conference);
    List<Team> findByNameContainingIgnoreCase(String name);
}
