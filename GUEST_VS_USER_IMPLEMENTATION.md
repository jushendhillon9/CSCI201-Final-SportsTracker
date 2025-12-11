# Guest vs User Differentiation Implementation Guide

## Overview
This document outlines the implementation of feature differentiation between guest users and authenticated users in the College Football Stat Tracker application.

## Current Implementation Status

### ✅ Completed
1. **User Permissions System** (`src/lib/userPermissions.ts`)
   - Centralized permission management
   - Functions for checking access rights
   - Helper functions for filtering data based on user role

2. **Frontend Components Updated**
   - `Dashboard.tsx` - Shows delayed data for guests, limited teams
   - `TeamsPage.tsx` - Filters to top 25 teams for guests
   - `GamesPage.tsx` - Applies 10-minute delay to live games for guests
   - `PlayersPage.tsx` - Shows basic stats for guests, advanced metrics for users
   - `Layout.tsx` - Displays guest limitations and blocks projections access

### 🔄 Partially Implemented
1. **Season Availability**
   - Logic exists in `getAvailableSeasons()` but needs integration with season selector UI
   - Backend API needs to filter by season based on user permissions

2. **Stats Depth**
   - Basic implementation in PlayersPage (shows/hides advanced metrics)
   - Needs expansion to other pages (Games, Teams)

### ❌ Not Yet Implemented
1. **Odds Data**
   - No odds data structure or API endpoints
   - No UI components for displaying odds
   - Need to create odds display component (hidden for guests)

2. **Backend API Integration**
   - All data currently uses mock data
   - Need to create API endpoints that respect user permissions
   - Need to implement delay logic on backend

## Feature Matrix

| Feature | Guest | Authenticated User |
|---------|-------|-------------------|
| **Real-time Delay** | 10 minutes | 0 minutes (real-time) |
| **Seasons Available** | 1 (current) | All seasons |
| **Teams Visible** | Top 25 only | All teams |
| **Projections** | ❌ Disabled | ✅ Enabled |
| **Odds Data** | ❌ Disabled | ✅ Enabled |
| **Stats Depth** | Basic metrics | Full (includes advanced) |

## Implementation Details

### 1. Real-time Delay (10 minutes for guests)

**How it works:**
- Guests see game data that is 10 minutes old
- Live games are filtered to only show those that started before `current_time - 10 minutes`
- Badge shows "DELAYED (10m)" instead of "LIVE"

**Implementation:**
```typescript
// In GamesPage.tsx and Dashboard.tsx
const delayedTimestamp = shouldApplyDelay(user) ? getDelayedTimestamp(user) : new Date();
const filteredGames = mockGames.filter(game => {
  if (game.status === 'live' && shouldApplyDelay(user)) {
    const gameDate = new Date(game.game_date);
    return gameDate < delayedTimestamp;
  }
  return true;
});
```

**Backend TODO:**
- When fetching live games, apply delay filter based on user role
- Return timestamp indicating data delay

### 2. Seasons Available

**How it works:**
- Guests can only view current season (2025)
- Authenticated users can view all historical seasons

**Frontend Implementation Needed:**
```typescript
// Add season selector component
const availableSeasons = getAvailableSeasons(user);
// Show dropdown with only available seasons
```

**Backend TODO:**
- Add `season` parameter to API endpoints
- Filter data by season based on user permissions
- Return error if guest tries to access non-current season

### 3. Teams Visible (Top 25 vs All)

**How it works:**
- Guests see only top 25 ranked teams
- Authenticated users see all teams

**Implementation:**
```typescript
// Already implemented in TeamsPage.tsx and Dashboard.tsx
const displayTeams = getVisibleTeams(user, mockTeams);
```

**Backend TODO:**
- When fetching teams, apply ranking filter for guests
- Return only top 25 teams sorted by ranking for guests

### 4. Projections Data

**How it works:**
- Projections page is disabled for guests (link grayed out)
- Only authenticated users can access projections

**Implementation:**
- Already implemented in `Layout.tsx` with `canAccessProjections()` check
- ProjectionsPage component exists but should show error for guests

**Backend TODO:**
- Return 403 Forbidden if guest tries to access projections API
- Add authentication check to projections endpoint

### 5. Odds Data

**How it works:**
- Odds data is completely hidden for guests
- Authenticated users can see betting odds for games

**Frontend Implementation Needed:**
1. Create `OddsData.tsx` component
2. Add odds display to GamesPage (only for authenticated users)
3. Create odds data structure in mockData.ts

**Backend TODO:**
- Create `/api/games/{gameId}/odds` endpoint
- Return 403 Forbidden for guests
- Return odds data for authenticated users

### 6. Stats Depth (Basic vs Full)

**How it works:**
- Guests see basic stats (yards, TDs, games played)
- Authenticated users see advanced metrics (yards/attempt, efficiency ratings, etc.)

**Current Implementation:**
- Partially implemented in PlayersPage.tsx
- Shows advanced metrics section only for authenticated users

**Needs Expansion:**
- Add advanced metrics to TeamsPage (offensive/defensive efficiency)
- Add advanced metrics to GamesPage (possession time, turnover margin)
- Create separate API endpoints for basic vs full stats

## Backend API Requirements

### Required Endpoints

1. **GET /api/teams**
   - Query params: `season`, `limit` (for guests)
   - Response: Filter teams based on user role
   - Guests: Return top 25 only, sorted by ranking

2. **GET /api/games**
   - Query params: `season`, `status`, `delay` (for guests)
   - Response: Filter games and apply delay for guests
   - Guests: Only return games older than 10 minutes if live

3. **GET /api/players/{playerId}/stats**
   - Query params: `season`, `depth` (basic/full)
   - Response: Return basic or full stats based on user role

4. **GET /api/projections**
   - Auth required: Yes
   - Response: 403 Forbidden for guests

5. **GET /api/games/{gameId}/odds**
   - Auth required: Yes
   - Response: 403 Forbidden for guests, odds data for users

6. **GET /api/seasons**
   - Response: List of available seasons based on user role
   - Guests: Return only current season
   - Users: Return all seasons

## Testing Checklist

- [ ] Guest sees only top 25 teams
- [ ] Guest sees 10-minute delayed live games
- [ ] Guest cannot access projections page
- [ ] Guest cannot see odds data
- [ ] Guest sees only basic stats
- [ ] Guest can only view current season
- [ ] Authenticated user sees all teams
- [ ] Authenticated user sees real-time data
- [ ] Authenticated user can access projections
- [ ] Authenticated user can see odds
- [ ] Authenticated user sees full stats
- [ ] Authenticated user can view all seasons

## Next Steps

1. **Create Odds Component**
   - Add odds data to mockData.ts
   - Create OddsDisplay component
   - Integrate into GamesPage

2. **Add Season Selector**
   - Create SeasonSelector component
   - Add to Dashboard, TeamsPage, GamesPage
   - Filter data based on selected season

3. **Backend Implementation**
   - Create API endpoints with permission checks
   - Implement delay logic on backend
   - Add authentication middleware

4. **Advanced Stats Expansion**
   - Add more advanced metrics to all pages
   - Create separate API endpoints for full stats
   - Add visual indicators for advanced metrics

5. **User Preferences**
   - Allow authenticated users to save preferences
   - Store favorite teams, players
   - Custom dashboard layouts

