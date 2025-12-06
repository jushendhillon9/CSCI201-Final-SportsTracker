// Mock data based on the database schema

export interface Team {
  teamid: number;
  name: string;
  conference: string;
  logo_url: string;
  ranking?: number;
  wins?: number;
  losses?: number;
}

export interface Player {
  playerid: number;
  name: string;
  teamid: number;
  position: string;
  teamName?: string;
  year?: string;
}

export interface Game {
  gameid: number;
  game_date: string;
  home_team: number;
  away_team: number;
  home_score?: number;
  away_score?: number;
  status: 'scheduled' | 'live' | 'completed';
  home_team_name?: string;
  away_team_name?: string;
}

export interface Stat {
  statid: number;
  playerid: number;
  gameid: number;
  season: number;
  stat_type: string;
  value: number;
  player_name?: string;
  team_name?: string;
}

export interface Projection {
  proj_id: number;
  playerid: number;
  next_gameid: number;
  projected_yards: number;
  projected_tds: number;
  confidence: number;
  player_name?: string;
  opponent?: string;
}

export const mockTeams: Team[] = [
  { teamid: 1, name: 'Georgia', conference: 'SEC', logo_url: '', ranking: 1, wins: 10, losses: 0 },
  { teamid: 2, name: 'Michigan', conference: 'Big Ten', logo_url: '', ranking: 2, wins: 10, losses: 0 },
  { teamid: 3, name: 'Ohio State', conference: 'Big Ten', logo_url: '', ranking: 3, wins: 9, losses: 1 },
  { teamid: 4, name: 'Alabama', conference: 'SEC', logo_url: '', ranking: 4, wins: 9, losses: 1 },
  { teamid: 5, name: 'USC', conference: 'Pac-12', logo_url: '', ranking: 5, wins: 9, losses: 1 },
  { teamid: 6, name: 'Clemson', conference: 'ACC', logo_url: '', ranking: 6, wins: 9, losses: 1 },
  { teamid: 7, name: 'Texas', conference: 'Big 12', logo_url: '', ranking: 7, wins: 8, losses: 2 },
  { teamid: 8, name: 'Penn State', conference: 'Big Ten', logo_url: '', ranking: 8, wins: 8, losses: 2 },
  { teamid: 9, name: 'Oregon', conference: 'Pac-12', logo_url: '', ranking: 9, wins: 8, losses: 2 },
  { teamid: 10, name: 'Florida State', conference: 'ACC', logo_url: '', ranking: 10, wins: 8, losses: 2 },
  { teamid: 11, name: 'LSU', conference: 'SEC', logo_url: '', ranking: 11, wins: 7, losses: 3 },
  { teamid: 12, name: 'Notre Dame', conference: 'Independent', logo_url: '', ranking: 12, wins: 7, losses: 3 },
  { teamid: 13, name: 'Oklahoma', conference: 'Big 12', logo_url: '', ranking: 13, wins: 7, losses: 3 },
  { teamid: 14, name: 'Tennessee', conference: 'SEC', logo_url: '', ranking: 14, wins: 7, losses: 3 },
  { teamid: 15, name: 'Utah', conference: 'Pac-12', logo_url: '', ranking: 15, wins: 7, losses: 3 },
  { teamid: 16, name: 'Washington', conference: 'Pac-12', logo_url: '', ranking: 16, wins: 7, losses: 3 },
  { teamid: 17, name: 'Kansas State', conference: 'Big 12', logo_url: '', ranking: 17, wins: 7, losses: 3 },
  { teamid: 18, name: 'Ole Miss', conference: 'SEC', logo_url: '', ranking: 18, wins: 6, losses: 4 },
  { teamid: 19, name: 'Tulane', conference: 'AAC', logo_url: '', ranking: 19, wins: 9, losses: 1 },
  { teamid: 20, name: 'UCLA', conference: 'Pac-12', logo_url: '', ranking: 20, wins: 6, losses: 4 },
  { teamid: 21, name: 'North Carolina', conference: 'ACC', logo_url: '', ranking: 21, wins: 6, losses: 4 },
  { teamid: 22, name: 'TCU', conference: 'Big 12', logo_url: '', ranking: 22, wins: 6, losses: 4 },
  { teamid: 23, name: 'Wisconsin', conference: 'Big Ten', logo_url: '', ranking: 23, wins: 6, losses: 4 },
  { teamid: 24, name: 'Kentucky', conference: 'SEC', logo_url: '', ranking: 24, wins: 6, losses: 4 },
  { teamid: 25, name: 'Mississippi State', conference: 'SEC', logo_url: '', ranking: 25, wins: 6, losses: 4 },
];

export const mockPlayers: Player[] = [
  { playerid: 1, name: 'Caleb Williams', teamid: 5, position: 'QB', teamName: 'USC', year: 'Junior' },
  { playerid: 2, name: 'Marvin Harrison Jr.', teamid: 3, position: 'WR', teamName: 'Ohio State', year: 'Junior' },
  { playerid: 3, name: 'Michael Penix Jr.', teamid: 16, position: 'QB', teamName: 'Washington', year: 'Senior' },
  { playerid: 4, name: 'Bo Nix', teamid: 9, position: 'QB', teamName: 'Oregon', year: 'Senior' },
  { playerid: 5, name: 'Jayden Daniels', teamid: 11, position: 'QB', teamName: 'LSU', year: 'Senior' },
  { playerid: 6, name: 'Rome Odunze', teamid: 16, position: 'WR', teamName: 'Washington', year: 'Junior' },
  { playerid: 7, name: 'Brock Bowers', teamid: 1, position: 'TE', teamName: 'Georgia', year: 'Junior' },
  { playerid: 8, name: 'JJ McCarthy', teamid: 2, position: 'QB', teamName: 'Michigan', year: 'Junior' },
  { playerid: 9, name: 'Blake Corum', teamid: 2, position: 'RB', teamName: 'Michigan', year: 'Senior' },
  { playerid: 10, name: 'Jalen Milroe', teamid: 4, position: 'QB', teamName: 'Alabama', year: 'Sophomore' },
];

export const mockGames: Game[] = [
  {
    gameid: 1,
    game_date: '2025-11-09T12:00:00',
    home_team: 1,
    away_team: 4,
    home_score: 24,
    away_score: 21,
    status: 'live',
    home_team_name: 'Georgia',
    away_team_name: 'Alabama',
  },
  {
    gameid: 2,
    game_date: '2025-11-09T15:30:00',
    home_team: 2,
    away_team: 8,
    status: 'scheduled',
    home_team_name: 'Michigan',
    away_team_name: 'Penn State',
  },
  {
    gameid: 3,
    game_date: '2025-11-09T19:00:00',
    home_team: 5,
    away_team: 9,
    status: 'scheduled',
    home_team_name: 'USC',
    away_team_name: 'Oregon',
  },
  {
    gameid: 4,
    game_date: '2025-11-02T12:00:00',
    home_team: 3,
    away_team: 6,
    home_score: 31,
    away_score: 17,
    status: 'completed',
    home_team_name: 'Ohio State',
    away_team_name: 'Clemson',
  },
  {
    gameid: 5,
    game_date: '2025-11-02T15:30:00',
    home_team: 11,
    away_team: 14,
    home_score: 28,
    away_score: 24,
    status: 'completed',
    home_team_name: 'LSU',
    away_team_name: 'Tennessee',
  },
];

export const mockStats: Stat[] = [
  { statid: 1, playerid: 1, gameid: 3, season: 2025, stat_type: 'passing_yards', value: 312, player_name: 'Caleb Williams', team_name: 'USC' },
  { statid: 2, playerid: 1, gameid: 3, season: 2025, stat_type: 'passing_tds', value: 3, player_name: 'Caleb Williams', team_name: 'USC' },
  { statid: 3, playerid: 2, gameid: 4, season: 2025, stat_type: 'receiving_yards', value: 156, player_name: 'Marvin Harrison Jr.', team_name: 'Ohio State' },
  { statid: 4, playerid: 2, gameid: 4, season: 2025, stat_type: 'receiving_tds', value: 2, player_name: 'Marvin Harrison Jr.', team_name: 'Ohio State' },
  { statid: 5, playerid: 9, gameid: 2, season: 2025, stat_type: 'rushing_yards', value: 142, player_name: 'Blake Corum', team_name: 'Michigan' },
  { statid: 6, playerid: 9, gameid: 2, season: 2025, stat_type: 'rushing_tds', value: 2, player_name: 'Blake Corum', team_name: 'Michigan' },
];

export const mockProjections: Projection[] = [
  {
    proj_id: 1,
    playerid: 1,
    next_gameid: 3,
    projected_yards: 295,
    projected_tds: 2.5,
    confidence: 0.87,
    player_name: 'Caleb Williams',
    opponent: 'vs Oregon',
  },
  {
    proj_id: 2,
    playerid: 2,
    next_gameid: 4,
    projected_yards: 135,
    projected_tds: 1.8,
    confidence: 0.82,
    player_name: 'Marvin Harrison Jr.',
    opponent: 'vs Michigan',
  },
  {
    proj_id: 3,
    playerid: 3,
    next_gameid: 2,
    projected_yards: 312,
    projected_tds: 3.1,
    confidence: 0.85,
    player_name: 'Michael Penix Jr.',
    opponent: 'vs UCLA',
  },
  {
    proj_id: 4,
    playerid: 9,
    next_gameid: 2,
    projected_yards: 118,
    projected_tds: 1.5,
    confidence: 0.79,
    player_name: 'Blake Corum',
    opponent: 'vs Penn State',
  },
];

// Helper functions
export const getTeamById = (teamid: number): Team | undefined => {
  return mockTeams.find(t => t.teamid === teamid);
};

export const getPlayersByTeam = (teamid: number): Player[] => {
  return mockPlayers.filter(p => p.teamid === teamid);
};

export const getGamesByDate = (date: string): Game[] => {
  return mockGames.filter(g => g.game_date.startsWith(date));
};
