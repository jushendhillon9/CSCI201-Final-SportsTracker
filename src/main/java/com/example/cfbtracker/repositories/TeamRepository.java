package com.example.cfbtracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.cfbtracker.models.Team;

public interface TeamRepository extends JpaRepository<Team, Integer> { }
