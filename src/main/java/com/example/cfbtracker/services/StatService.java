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

    public Stat saveStat(Stat stat) {
        return statRepository.save(stat);
    }
}
