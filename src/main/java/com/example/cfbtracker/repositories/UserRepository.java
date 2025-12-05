package com.example.cfbtracker.repositories;

import com.example.cfbtracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {}
