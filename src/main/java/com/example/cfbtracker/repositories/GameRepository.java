package com.example.cfbtracker.repositories;

import com.example.cfbtracker.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Integer> {}
