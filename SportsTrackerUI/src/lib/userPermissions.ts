import { type User } from '../App';

export interface UserPermissions {
  realtimeDelayMinutes: number;
  seasonsAvailable: number | 'all';
  teamsVisible: number | 'all';
  projectionsEnabled: boolean;
  oddsDataEnabled: boolean;
  statsDepth: 'basic' | 'full';
}

export function getUserPermissions(user: User | null): UserPermissions {
  if (!user || user.role === 'guest') {
    return {
      realtimeDelayMinutes: 10,
      seasonsAvailable: 1,
      teamsVisible: 25, // Top 25 only
      projectionsEnabled: false,
      oddsDataEnabled: false,
      statsDepth: 'basic',
    };
  }

  // Authenticated user
  return {
    realtimeDelayMinutes: 0,
    seasonsAvailable: 'all',
    teamsVisible: 'all',
    projectionsEnabled: true,
    oddsDataEnabled: true,
    statsDepth: 'full',
  };
}

export function shouldApplyDelay(user: User | null): boolean {
  return getUserPermissions(user).realtimeDelayMinutes > 0;
}

export function getDelayedTimestamp(user: User | null): Date {
  const delay = getUserPermissions(user).realtimeDelayMinutes;
  const now = new Date();
  return new Date(now.getTime() - delay * 60 * 1000);
}

export function canAccessProjections(user: User | null): boolean {
  return getUserPermissions(user).projectionsEnabled;
}

export function canAccessOdds(user: User | null): boolean {
  return getUserPermissions(user).oddsDataEnabled;
}

export function getAvailableSeasons(user: User | null): number[] {
  const permissions = getUserPermissions(user);
  if (permissions.seasonsAvailable === 'all') {
    // Return all available seasons (you'll need to fetch this from API)
    return [2025, 2024, 2023, 2022]; // Example
  }
  // Return only the current season
  return [2025]; // Current season
}

export function getVisibleTeams(user: User | null, allTeams: any[]): any[] {
  const permissions = getUserPermissions(user);
  if (permissions.teamsVisible === 'all') {
    return allTeams;
  }
  // Return top N teams (sorted by ranking; include unranked at the end)
  return allTeams
    .sort((a, b) => (a.ranking || 9999) - (b.ranking || 9999))
    .slice(0, permissions.teamsVisible as number);
}
