import { useEffect, useState } from 'react';
import { type User } from '../App';
import { Card, CardContent, CardHeader, CardTitle } from './figma-ui/card';
import { Badge } from './figma-ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './figma-ui/tabs';
import { Calendar, Clock, CheckCircle } from 'lucide-react';
import { shouldApplyDelay, getDelayedTimestamp, getUserPermissions } from '../lib/userPermissions';
import { api } from '../lib/api';

interface GamesPageProps {
  user: User;
}

type GameDto = {
  gameId?: number;
  gameid?: number; // fallback for older responses
  season?: number;
  week?: number;
  date?: string;
  homeTeam?: string;
  awayTeam?: string;
  homeLogo?: string;
  awayLogo?: string;
  homeScore?: number;
  awayScore?: number;
  status?: string;
  venue?: string;
  quarter?: string;
  timeRemaining?: string;
};

export default function GamesPage({ user }: GamesPageProps) {
  const permissions = getUserPermissions(user);
  const [games, setGames] = useState<GameDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // Apply delay for guests - filter live games to show only those before the delayed timestamp
  const delayedTimestamp = shouldApplyDelay(user) ? getDelayedTimestamp(user) : new Date();
  const filteredGames = games.filter(game => {
    if ((game.status === 'live' || game.status === 'in_progress') && shouldApplyDelay(user)) {
      const gameDate = new Date((game.date || '').replace(' ', 'T'));
      return gameDate < delayedTimestamp;
    }
    return true;
  });
  
  const liveGames = filteredGames.filter(g => g.status === 'live' || g.status === 'in_progress');
  const scheduledGames = filteredGames.filter(g => g.status === 'scheduled');
  const completedGames = filteredGames.filter(g => g.status === 'completed' || g.status === 'final');

  useEffect(() => {
    setLoading(true);
    api.getGames()
      .then(setGames)
      .catch(err => setError(err.message || 'Failed to load games'))
      .finally(() => setLoading(false));
  }, []);

  const formatDate = (dateString: string | undefined) => {
    if (!dateString) return 'TBD';
    const date = new Date(dateString.replace(' ', 'T'));
    if (isNaN(date.getTime())) return 'TBD';
    return date.toLocaleDateString('en-US', { 
      weekday: 'short',
      month: 'short', 
      day: 'numeric',
      year: 'numeric' 
    });
  };

  const formatTime = (dateString: string | undefined) => {
    if (!dateString) return 'TBD';
    const date = new Date(dateString.replace(' ', 'T'));
    if (isNaN(date.getTime())) return 'TBD';
    return date.toLocaleTimeString('en-US', { 
      hour: 'numeric',
      minute: '2-digit',
      timeZone: 'America/New_York'
    });
  };

  const GameCard = ({ game }: { game: GameDto }) => {
    const isLive = game.status === 'live' || game.status === 'in_progress';
    const isCompleted = game.status === 'completed' || game.status === 'final';

    return (
      <Card className={`${isLive ? 'border-red-300 bg-red-50' : ''}`}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Calendar className="h-4 w-4 text-gray-500" />
              <span className="text-sm text-gray-600">{formatDate(game.date)}</span>
            </div>
            {isLive && (
              <Badge className={shouldApplyDelay(user) ? "bg-orange-600" : "bg-red-600"}>
                {shouldApplyDelay(user) ? `DELAYED (${permissions.realtimeDelayMinutes}m)` : 'LIVE'}
              </Badge>
            )}
            {isCompleted && (
              <Badge variant="outline" className="text-green-600 border-green-300">
                <CheckCircle className="h-3 w-3 mr-1" />
                Final
              </Badge>
            )}
            {game.status === 'scheduled' && (
              <Badge variant="outline">
                <Clock className="h-3 w-3 mr-1" />
                {formatTime(game.date)} ET
              </Badge>
            )}
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {/* Away Team */}
            <div className="flex items-center justify-between p-3 bg-white rounded-lg">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center overflow-hidden">
                  {game.awayLogo ? (
                    <img
                      src={game.awayLogo}
                      alt={`${game.awayTeam ?? 'Away team'} logo`}
                      className="w-full h-full object-contain"
                      onError={(e) => { (e.target as HTMLImageElement).style.display = 'none'; }}
                    />
                  ) : (
                    <span className="text-lg">FB</span>
                  )}
                </div>
                <div>
                  <div className="text-gray-900">{game.awayTeam ?? 'Away'}</div>
                  <div className="text-xs text-gray-500">Away</div>
                </div>
              </div>
              {(isLive || isCompleted) && (
                <div className="text-3xl text-gray-900">{game.awayScore ?? 0}</div>
              )}
            </div>

            {/* VS Divider */}
            <div className="flex items-center justify-center">
              <div className="text-sm text-gray-500 bg-gray-100 px-3 py-1 rounded-full">
                VS
              </div>
            </div>

            {/* Home Team */}
            <div className="flex items-center justify-between p-3 bg-white rounded-lg">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center overflow-hidden">
                  {game.homeLogo ? (
                    <img
                      src={game.homeLogo}
                      alt={`${game.homeTeam ?? 'Home team'} logo`}
                      className="w-full h-full object-contain"
                      onError={(e) => { (e.target as HTMLImageElement).style.display = 'none'; }}
                    />
                  ) : (
                    <span className="text-lg">FB</span>
                  )}
                </div>
                <div>
                  <div className="text-gray-900">{game.homeTeam ?? 'Home'}</div>
                  <div className="text-xs text-gray-500">Home</div>
                </div>
              </div>
              {(isLive || isCompleted) && (
                <div className="text-3xl text-gray-900">{game.homeScore ?? 0}</div>
              )}
            </div>

            {/* Game Info */}
            {isLive && (
              <div className="pt-3 border-t text-center">
                <Badge variant="outline" className="text-red-600 border-red-300">
                  {`${game.quarter ?? ''} ${game.timeRemaining ?? ''}`.trim() || 'In progress'}
                </Badge>
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    );
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 mb-2">Games</h1>
        <p className="text-gray-600">
          View live scores, upcoming matchups, and game results
        </p>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Live Now</CardTitle>
            <div className="h-3 w-3 bg-red-600 rounded-full animate-pulse" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{liveGames.length}</div>
            <p className="text-xs text-gray-500 mt-1">Games in progress</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Upcoming</CardTitle>
            <Clock className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{scheduledGames.length}</div>
            <p className="text-xs text-gray-500 mt-1">Scheduled games</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Completed</CardTitle>
            <CheckCircle className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{completedGames.length}</div>
            <p className="text-xs text-gray-500 mt-1">Final scores</p>
          </CardContent>
        </Card>
      </div>

      {/* Games Tabs */}
      <Tabs defaultValue={liveGames.length > 0 ? 'live' : 'scheduled'} className="space-y-4">
        <TabsList>
          <TabsTrigger value="live" className="flex items-center gap-2">
            {liveGames.length > 0 && (
              <div className="h-2 w-2 bg-red-600 rounded-full animate-pulse" />
            )}
            Live ({liveGames.length})
          </TabsTrigger>
          <TabsTrigger value="scheduled">
            Scheduled ({scheduledGames.length})
          </TabsTrigger>
          <TabsTrigger value="completed">
            Completed ({completedGames.length})
          </TabsTrigger>
        </TabsList>

        <TabsContent value="live" className="space-y-4">
          {loading && <div className="text-gray-500">Loading games...</div>}
          {error && <div className="text-red-600">{error}</div>}
          {!loading && !error && liveGames.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {liveGames.map(game => (
                <GameCard key={game.gameId ?? game.gameid} game={game} />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <div className="text-4xl mb-4">FB</div>
                <p className="text-gray-500">No live games at the moment</p>
                <p className="text-sm text-gray-400 mt-2">Check back during game days</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="scheduled" className="space-y-4">
          {loading && <div className="text-gray-500">Loading games...</div>}
          {error && <div className="text-red-600">{error}</div>}
          {!loading && !error && scheduledGames.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {scheduledGames.map(game => (
                <GameCard key={game.gameId ?? game.gameid} game={game} />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <p className="text-gray-500">No scheduled games</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="completed" className="space-y-4">
          {loading && <div className="text-gray-500">Loading games...</div>}
          {error && <div className="text-red-600">{error}</div>}
          {!loading && !error && completedGames.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {completedGames.map(game => (
                <GameCard key={game.gameId ?? game.gameid} game={game} />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <p className="text-gray-500">No completed games</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
