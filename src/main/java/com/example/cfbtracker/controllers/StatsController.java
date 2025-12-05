package com.example.cfbtracker.controllers;

import com.example.cfbtracker.models.Stat;
import com.example.cfbtracker.repositories.StatRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatRepository statRepository;

    public StatsController(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    @GetMapping
    public List<Stat> getAllStats() {
        return statRepository.findAll();
    }
}
