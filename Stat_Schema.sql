
CREATE DATABASE IF NOT EXISTS college_football_stats;
USE college_football_stats;
DROP TABLE IF EXISTS Stats;
DROP TABLE IF EXISTS Games;
DROP TABLE IF EXISTS Players;
DROP TABLE IF EXISTS Teams;
DROP TABLE IF EXISTS Users;



-- USERS
CREATE TABLE Users (
    userid      INT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(100),
    google_id   VARCHAR(255) UNIQUE,       -- optional, for Google SSO
    role        ENUM('guest','user','premium','admin') NOT NULL DEFAULT 'user',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TEAMS
CREATE TABLE Teams (
    teamid      INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    conference  VARCHAR(100),
    logo_url    VARCHAR(500)
);

-- PLAYERS
CREATE TABLE Players (
    playerid INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    teamid   INT NOT NULL,
    position VARCHAR(50),
    jersey_number INT,
    FOREIGN KEY (teamid) REFERENCES Teams(teamid)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- GAMES
CREATE TABLE Games (
    gameid        INT AUTO_INCREMENT PRIMARY KEY,
    season        INT NOT NULL,
    week          INT,
    game_date     DATETIME NOT NULL,
    home_team_id  INT NOT NULL,
    away_team_id  INT NOT NULL,
    home_score    INT DEFAULT 0,
    away_score    INT DEFAULT 0,
    venue         VARCHAR(255),
    FOREIGN KEY (home_team_id) REFERENCES Teams(teamid)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (away_team_id) REFERENCES Teams(teamid)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- STATS (per player, per game)
CREATE TABLE Stats (
    statid    INT AUTO_INCREMENT PRIMARY KEY,
    playerid  INT NOT NULL,
    gameid    INT NOT NULL,
    season    INT NOT NULL,
    stat_type VARCHAR(50) NOT NULL,    -- e.g. 'passing_yards', 'TD', etc.
    value     DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (playerid) REFERENCES Players(playerid)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (gameid) REFERENCES Games(gameid)
        ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE INDEX idx_players_teamid
    ON Players(teamid);

CREATE INDEX idx_games_season_date
    ON Games(season, game_date);

CREATE INDEX idx_stats_player_game
    ON Stats(playerid, gameid);

CREATE INDEX idx_stats_stat_type
    ON Stats(stat_type);
    
    
-- SEED TO TEST CUZ NO API

INSERT INTO Teams (name, conference) VALUES
('USC', 'Pac-12'),
('Ohio State', 'Big Ten');

INSERT INTO Players (name, teamid, position) VALUES
('Caleb Williams', 1, 'QB'),
('Marvin Harrison Jr', 2, 'WR');

INSERT INTO Games (season, week, game_date, home_team_id, away_team_id, home_score, away_score)
VALUES (2024, 1, '2024-09-01 17:00:00', 1, 2, 28, 35);

INSERT INTO Stats (playerid, gameid, season, stat_type, value)
VALUES
(1, 1, 2024, 'passing_yards', 320),
(2, 1, 2024, 'receiving_yards', 145);

SELECT '=== Teams ===' AS '';
SELECT * FROM Teams;

SELECT '=== Players ===' AS '';
SELECT * FROM Players;

SELECT '=== Games ===' AS '';
SELECT * FROM Games;

SELECT '=== Stats ===' AS '';
SELECT * FROM Stats;

