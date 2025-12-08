CREATE DATABASE IF NOT EXISTS sportstracker;

USE sportstracker;

-- Users table
CREATE TABLE IF NOT EXISTS Users (
    userid INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) DEFAULT 'guest',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);

-- Teams table
CREATE TABLE IF NOT EXISTS Teams (
    teamid INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    conference VARCHAR(50),
    logo_url VARCHAR(255),
    UNIQUE KEY unique_team (name, conference),
    INDEX idx_conference (conference)
);

-- Players table
CREATE TABLE IF NOT EXISTS Players (
    playerid INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    teamid INT NOT NULL,
    position VARCHAR(20),
    FOREIGN KEY (teamid) REFERENCES Teams(teamid) ON DELETE CASCADE,
    INDEX idx_teamid (teamid),
    INDEX idx_position (position)
);

-- Games table
CREATE TABLE IF NOT EXISTS Games (
    gameid INT PRIMARY KEY AUTO_INCREMENT,
    game_date DATETIME NOT NULL,
    home_team INT NOT NULL,
    away_team INT NOT NULL,
    scores TEXT,
    key_statistics TEXT,
    FOREIGN KEY (home_team) REFERENCES Teams(teamid) ON DELETE CASCADE,
    FOREIGN KEY (away_team) REFERENCES Teams(teamid) ON DELETE CASCADE,
    INDEX idx_game_date (game_date),
    INDEX idx_home_team (home_team),
    INDEX idx_away_team (away_team)
);

-- Stats table (player stats per game)
CREATE TABLE IF NOT EXISTS Stats (
    statid INT PRIMARY KEY AUTO_INCREMENT,
    playerid INT NOT NULL,
    gameid INT NOT NULL,
    season INT NOT NULL,
    stat_type VARCHAR(50) NOT NULL,
    value FLOAT,
    FOREIGN KEY (playerid) REFERENCES Players(playerid) ON DELETE CASCADE,
    FOREIGN KEY (gameid) REFERENCES Games(gameid) ON DELETE CASCADE,
    INDEX idx_playerid (playerid),
    INDEX idx_gameid (gameid),
    INDEX idx_season (season),
    INDEX idx_stat_type (stat_type)
);

-- Projections table
CREATE TABLE IF NOT EXISTS Projections (
    proj_id INT PRIMARY KEY AUTO_INCREMENT,
    playerid INT NOT NULL,
    next_gameid INT NOT NULL,
    projected_yards FLOAT,
    projected_tds FLOAT,
    confidence FLOAT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (playerid) REFERENCES Players(playerid) ON DELETE CASCADE,
    FOREIGN KEY (next_gameid) REFERENCES Games(gameid) ON DELETE CASCADE,
    INDEX idx_playerid (playerid),
    INDEX idx_next_gameid (next_gameid)
);

-- API_Log table (for debugging ingestion)
CREATE TABLE IF NOT EXISTS API_Log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    source VARCHAR(50),
    fetched_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    message TEXT,
    INDEX idx_fetched_at (fetched_at),
    INDEX idx_status (status)
);
