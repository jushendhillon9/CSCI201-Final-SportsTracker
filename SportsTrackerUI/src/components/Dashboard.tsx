import { type User } from '../App';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './figma-ui/card';
import { Badge } from './figma-ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './figma-ui/tabs';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { Users, Calendar, Trophy, Clock } from 'lucide-react';
import { mockTeams, mockGames, mockPlayers } from '../lib/mockData';
import { getVisibleTeams, getUserPermissions, shouldApplyDelay, getDelayedTimestamp } from '../lib/userPermissions';

interface DashboardProps {
  user: User;
}

export default function Dashboard({ user }: DashboardProps) {
  const permissions = getUserPermissions(user);
  const displayTeams = getVisibleTeams(user, mockTeams);
  
  // Apply delay for guests - filter games to show only those before the delayed timestamp
  const delayedTimestamp = shouldApplyDelay(user) ? getDelayedTimestamp(user) : new Date();
  const filteredGames = mockGames.filter(game => {
    if (game.status === 'live' && shouldApplyDelay(user)) {
      // For live games, only show if they started before the delay threshold
      const gameDate = new Date(game.game_date);
      return gameDate < delayedTimestamp;
    }
    return true;
  });

  // Stats data for charts
  const conferenceData = [
    { name: 'SEC', teams: displayTeams.filter(t => t.conference === 'SEC').length },
    { name: 'Big Ten', teams: displayTeams.filter(t => t.conference === 'Big Ten').length },
    { name: 'Pac-12', teams: displayTeams.filter(t => t.conference === 'Pac-12').length },
    { name: 'ACC', teams: displayTeams.filter(t => t.conference === 'ACC').length },
    { name: 'Big 12', teams: displayTeams.filter(t => t.conference === 'Big 12').length },
  ];

  const topTeamsData = displayTeams.slice(0, 10).map(t => ({
    name: t.name,
    wins: t.wins || 0,
  }));

  const liveGames = filteredGames.filter(g => g.status === 'live');
  const todayGames = filteredGames.filter(g => g.game_date.startsWith('2025-11-09'));

  const COLORS = ['#2563eb', '#dc2626', '#16a34a', '#ea580c', '#8b5cf6'];

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 mb-2">
          Dashboard
        </h1>
        <p className="text-gray-600">
          {user.role === 'guest' 
            ? "Welcome Guest. Here's what's happening in college football today."
            : `Welcome back, ${user.name || user.email}. Here's what's happening in college football today.`}
        </p>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Live Games</CardTitle>
            <Clock className="h-4 w-4 text-red-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{liveGames.length}</div>
            <p className="text-xs text-gray-500 mt-1">
              In progress now
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Today's Games</CardTitle>
            <Calendar className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{todayGames.length}</div>
            <p className="text-xs text-gray-500 mt-1">
              Scheduled for today
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Tracked Teams</CardTitle>
            <Users className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{displayTeams.length}</div>
            <p className="text-xs text-gray-500 mt-1">
              {permissions.teamsVisible === 'all' ? 'All teams' : `Top ${permissions.teamsVisible} only`}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Top Players</CardTitle>
            <Trophy className="h-4 w-4 text-yellow-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{mockPlayers.length}</div>
            <p className="text-xs text-gray-500 mt-1">
              Tracking performance
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Live Games Section */}
      {liveGames.length > 0 && (
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle>Live Games</CardTitle>
                <CardDescription>
                  {shouldApplyDelay(user) 
                    ? `Delayed updates (${permissions.realtimeDelayMinutes} min delay)` 
                    : 'Real-time scores and updates'}
                </CardDescription>
              </div>
              <Badge className="bg-red-600">
                {shouldApplyDelay(user) ? `DELAYED` : 'LIVE'}
              </Badge>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {liveGames.map(game => (
                <div key={game.gameid} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                  <div className="flex-1">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-gray-900">{game.away_team_name}</span>
                      <span className="text-2xl">{game.away_score}</span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-gray-900">{game.home_team_name}</span>
                      <span className="text-2xl">{game.home_score}</span>
                    </div>
                  </div>
                  <div className="ml-6 text-right">
                    <Badge variant="outline" className="text-red-600 border-red-300">
                      Q3 8:45
                    </Badge>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Charts Section */}
      <Tabs defaultValue="rankings" className="space-y-4">
        <TabsList>
          <TabsTrigger value="rankings">Top 10 Rankings</TabsTrigger>
          <TabsTrigger value="conferences">Conference Distribution</TabsTrigger>
        </TabsList>

        <TabsContent value="rankings" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Top 10 Teams by Wins</CardTitle>
              <CardDescription>Current season performance</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={350}>
                <BarChart data={topTeamsData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" angle={-45} textAnchor="end" height={100} />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="wins" fill="#2563eb" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="conferences" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Teams by Conference</CardTitle>
              <CardDescription>Distribution across conferences</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid md:grid-cols-2 gap-6">
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={conferenceData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={(props: any) => props.name || ''}
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="teams"
                    >
                      {conferenceData.map((_, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>

                <div className="space-y-3">
                  {conferenceData.map((conf, index) => (
                    <div key={conf.name} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                      <div className="flex items-center gap-3">
                        <div
                          className="w-4 h-4 rounded"
                          style={{ backgroundColor: COLORS[index % COLORS.length] }}
                        />
                        <span className="text-gray-900">{conf.name}</span>
                      </div>
                      <span className="text-gray-600">{conf.teams} teams</span>
                    </div>
                  ))}
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Upcoming Games */}
      <Card>
        <CardHeader>
          <CardTitle>Upcoming Games Today</CardTitle>
          <CardDescription>Scheduled matchups for November 9, 2025</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {todayGames.filter(g => g.status === 'scheduled').map(game => (
              <div key={game.gameid} className="flex items-center justify-between p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="text-gray-900">{game.away_team_name}</span>
                    <span className="text-gray-500">@</span>
                    <span className="text-gray-900">{game.home_team_name}</span>
                  </div>
                  <p className="text-sm text-gray-500">
                    {new Date(game.game_date).toLocaleTimeString('en-US', {
                      hour: 'numeric',
                      minute: '2-digit',
                      timeZone: 'America/New_York'
                    })} ET
                  </p>
                </div>
                <Badge variant="outline">Scheduled</Badge>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
