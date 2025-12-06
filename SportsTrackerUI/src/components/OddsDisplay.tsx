import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './figma-ui/card';
import { Badge } from './figma-ui/badge';
import { TrendingUp, TrendingDown } from 'lucide-react';
import { canAccessOdds } from '../lib/userPermissions';
import { type User } from '../App';

export interface OddsData {
  gameid: number;
  home_team_odds: number; // Moneyline odds (e.g., -150)
  away_team_odds: number; // Moneyline odds (e.g., +130)
  spread: number; // Point spread (e.g., -3.5)
  over_under: number; // Total points (e.g., 45.5)
  last_updated: string;
}

interface OddsDisplayProps {
  user: User | null;
  gameId?: number;
  homeTeamName: string;
  awayTeamName: string;
  odds?: OddsData;
}

export default function OddsDisplay({ user, gameId, homeTeamName, awayTeamName, odds }: OddsDisplayProps) {
  if (!canAccessOdds(user)) {
    return null; // Don't render for guests
  }

  if (!odds) {
    return (
      <Card className="border-blue-200 bg-blue-50">
        <CardContent className="pt-6">
          <p className="text-sm text-gray-500 text-center">Odds data not available</p>
        </CardContent>
      </Card>
    );
  }

  const formatOdds = (odds: number) => {
    if (odds > 0) {
      return `+${odds}`;
    }
    return odds.toString();
  };

  const getOddsColor = (odds: number) => {
    if (odds > 0) return 'text-green-600';
    return 'text-red-600';
  };

  return (
    <Card className="border-blue-200 bg-blue-50">
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="text-sm">Betting Odds</CardTitle>
          <Badge variant="outline" className="text-xs">
            {new Date(odds.last_updated).toLocaleTimeString('en-US', {
              hour: 'numeric',
              minute: '2-digit'
            })}
          </Badge>
        </div>
        <CardDescription className="text-xs">Live odds - Sign in required</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* Moneyline */}
        <div>
          <div className="text-xs text-gray-600 mb-2">Moneyline</div>
          <div className="grid grid-cols-2 gap-2">
            <div className="bg-white p-2 rounded text-center">
              <div className="text-xs text-gray-500 mb-1">{awayTeamName}</div>
              <div className={`font-bold ${getOddsColor(odds.away_team_odds)}`}>
                {formatOdds(odds.away_team_odds)}
              </div>
            </div>
            <div className="bg-white p-2 rounded text-center">
              <div className="text-xs text-gray-500 mb-1">{homeTeamName}</div>
              <div className={`font-bold ${getOddsColor(odds.home_team_odds)}`}>
                {formatOdds(odds.home_team_odds)}
              </div>
            </div>
          </div>
        </div>

        {/* Spread */}
        <div>
          <div className="text-xs text-gray-600 mb-2">Spread</div>
          <div className="bg-white p-3 rounded text-center">
            <div className="flex items-center justify-center gap-2">
              <span className="text-sm font-semibold">
                {homeTeamName} {odds.spread > 0 ? '+' : ''}{odds.spread}
              </span>
            </div>
          </div>
        </div>

        {/* Over/Under */}
        <div>
          <div className="text-xs text-gray-600 mb-2">Total</div>
          <div className="bg-white p-3 rounded">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <TrendingUp className="h-4 w-4 text-green-600" />
                <span className="text-sm font-semibold">Over {odds.over_under}</span>
              </div>
              <div className="flex items-center gap-2">
                <TrendingDown className="h-4 w-4 text-red-600" />
                <span className="text-sm font-semibold">Under {odds.over_under}</span>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

