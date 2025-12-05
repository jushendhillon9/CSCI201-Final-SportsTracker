package com.example.cfbtracker.repositories;

import com.example.cfbtracker.models.Stat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatRepository extends JpaRepository<Stat, Integer> {}
