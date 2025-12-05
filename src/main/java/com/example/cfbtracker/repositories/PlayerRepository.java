package com.example.cfbtracker.repositories;

import com.example.cfbtracker.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Integer> {}
