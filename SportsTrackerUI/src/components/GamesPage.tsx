import { type User } from '../App';
import { Card, CardContent, CardHeader, CardTitle } from './figma-ui/card';
import { Badge } from './figma-ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './figma-ui/tabs';
import { Calendar, Clock, CheckCircle } from 'lucide-react';
import { mockGames } from '../lib/mockData';
import { shouldApplyDelay, getDelayedTimestamp, getUserPermissions } from '../lib/userPermissions';

interface GamesPageProps {
  user: User;
}

export default function GamesPage({ user }: GamesPageProps) {
  const permissions = getUserPermissions(user);
  
  // Apply delay for guests - filter live games to show only those before the delayed timestamp
  const delayedTimestamp = shouldApplyDelay(user) ? getDelayedTimestamp(user) : new Date();
  const filteredGames = mockGames.filter(game => {
    if (game.status === 'live' && shouldApplyDelay(user)) {
      const gameDate = new Date(game.game_date);
      return gameDate < delayedTimestamp;
    }
    return true;
  });
  
  const liveGames = filteredGames.filter(g => g.status === 'live');
  const scheduledGames = filteredGames.filter(g => g.status === 'scheduled');
  const completedGames = filteredGames.filter(g => g.status === 'completed');

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      weekday: 'short',
      month: 'short', 
      day: 'numeric',
      year: 'numeric' 
    });
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', { 
      hour: 'numeric',
      minute: '2-digit',
      timeZone: 'America/New_York'
    });
  };

  const GameCard = ({ game }: { game: typeof mockGames[0] }) => {
    const isLive = game.status === 'live';
    const isCompleted = game.status === 'completed';

    return (
      <Card className={`${isLive ? 'border-red-300 bg-red-50' : ''}`}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Calendar className="h-4 w-4 text-gray-500" />
              <span className="text-sm text-gray-600">{formatDate(game.game_date)}</span>
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
                {formatTime(game.game_date)} ET
              </Badge>
            )}
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {/* Away Team */}
            <div className="flex items-center justify-between p-3 bg-white rounded-lg">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">
                  🏈
                </div>
                <div>
                  <div className="text-gray-900">{game.away_team_name}</div>
                  <div className="text-xs text-gray-500">Away</div>
                </div>
              </div>
              {(isLive || isCompleted) && (
                <div className="text-3xl text-gray-900">{game.away_score}</div>
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
                <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">
                  🏈
                </div>
                <div>
                  <div className="text-gray-900">{game.home_team_name}</div>
                  <div className="text-xs text-gray-500">Home</div>
                </div>
              </div>
              {(isLive || isCompleted) && (
                <div className="text-3xl text-gray-900">{game.home_score}</div>
              )}
            </div>

            {/* Game Info */}
            {isLive && (
              <div className="pt-3 border-t text-center">
                <Badge variant="outline" className="text-red-600 border-red-300">
                  Q3 • 8:45 remaining
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
          {liveGames.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {liveGames.map(game => (
                <GameCard key={game.gameid} game={game} />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <div className="text-4xl mb-4">📺</div>
                <p className="text-gray-500">No live games at the moment</p>
                <p className="text-sm text-gray-400 mt-2">Check back during game days</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="scheduled" className="space-y-4">
          {scheduledGames.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {scheduledGames.map(game => (
                <GameCard key={game.gameid} game={game} />
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
          {completedGames.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {completedGames.map(game => (
                <GameCard key={game.gameid} game={game} />
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
