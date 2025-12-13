import { useEffect, useState } from 'react';
import { type User } from '../App';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './figma-ui/card';
import { Input } from './figma-ui/input';
import { Badge } from './figma-ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './figma-ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './figma-ui/table';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './figma-ui/tabs';
import { Search, User as UserIcon } from 'lucide-react';
import { getUserPermissions } from '../lib/userPermissions';
import { api } from '../lib/api';

interface PlayersPageProps {
  user: User;
}

export default function PlayersPage({ user }: PlayersPageProps) {
  const permissions = getUserPermissions(user);
  const [searchQuery, setSearchQuery] = useState('');
  const [positionFilter, setPositionFilter] = useState<string>('all');
  const [players, setPlayers] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const MAX_PLAYERS = 25;

  const positions = ['all', ...new Set(players.map(p => p.position).filter(Boolean))];

  // Aggregate stats if provided in payload
  const playerStats = players.map(player => {
    const playerId = player.playerId ?? player.playerid;
    const jerseyNumber = player.jerseyNumber ?? player.jersey_number;
    return {
      ...player,
      playerId,
      jerseyNumber,
      passingYards: player.passingYards ?? player.passing_yards ?? 0,
      rushingYards: player.rushingYards ?? player.rushing_yards ?? 0,
      receivingYards: player.receivingYards ?? player.receiving_yards ?? 0,
      totalTDs: player.totalTDs ?? player.total_tds ?? 0,
      gamesPlayed: player.gamesPlayed ?? player.games_played ?? 0,
    };
  });

  const fetchPlayers = (params?: { search?: string; position?: string }) => {
    setLoading(true);
    setError(null);
    api.getPlayers({
      season: String(new Date().getFullYear()),
      search: params?.search,
      position: params?.position && params.position !== 'all' ? params.position : undefined,
      limit: MAX_PLAYERS,
      sort: 'tds'
    })
      .then(setPlayers)
      .catch(err => setError(err.message || 'Failed to load players'))
      .finally(() => setLoading(false));
  };

  // Initial load: top 25 by TDs
  useEffect(() => {
    fetchPlayers();
  }, []);

  const filteredPlayers = playerStats.filter(p => {
    const matchesSearch = !searchQuery || p.name.toLowerCase().includes(searchQuery.toLowerCase()) || p.teamName?.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesPosition = positionFilter === 'all' || p.position === positionFilter;
    return matchesSearch && matchesPosition;
  });

  const positionColors: Record<string, string> = {
    QB: 'bg-blue-100 text-blue-700 border-blue-300',
    RB: 'bg-green-100 text-green-700 border-green-300',
    WR: 'bg-purple-100 text-purple-700 border-purple-300',
    TE: 'bg-orange-100 text-orange-700 border-orange-300',
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 mb-2">Players</h1>
        <p className="text-gray-600">
          Track individual player performance and statistics
        </p>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                placeholder="Search players or teams..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Select value={positionFilter} onValueChange={setPositionFilter}>
              <SelectTrigger className="w-full md:w-[200px]">
                <SelectValue placeholder="Position" />
              </SelectTrigger>
              <SelectContent>
                {positions.map(pos => (
                  <SelectItem key={pos} value={pos}>
                    {pos === 'all' ? 'All Positions' : pos}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Players Content */}
      <Tabs defaultValue="cards" className="space-y-4">
        <TabsList>
          <TabsTrigger value="cards">Card View</TabsTrigger>
          <TabsTrigger value="table">Table View</TabsTrigger>
        </TabsList>

        <TabsContent value="cards" className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {loading && <div className="text-gray-500">Loading players...</div>}
            {error && <div className="text-red-600">{error}</div>}
            {!loading && !error && filteredPlayers.map(player => {
              const stats = player;
              return (
                <Card key={player.playerId ?? player.playerid ?? player.name} className="hover:shadow-lg transition-shadow">
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <CardTitle className="text-lg mb-2">{player.name}</CardTitle>
                        <div className="flex items-center gap-2">
                          <Badge variant="outline" className={positionColors[player.position] || ''}>
                            {player.position}
                          </Badge>
                          {stats.jerseyNumber && (
                            <span className="text-sm text-gray-600">#{stats.jerseyNumber}</span>
                          )}
                        </div>
                      </div>
                      <div className="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
                        <UserIcon className="h-6 w-6 text-gray-400" />
                      </div>
                    </div>
                    <CardDescription className="mt-2">{player.teamName}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-2">
                      {player.position === 'QB' && stats?.passingYards! > 0 && (
                        <div className="flex justify-between text-sm">
                          <span className="text-gray-600">Passing Yards</span>
                          <span className="text-gray-900">{stats?.passingYards}</span>
                        </div>
                      )}
                      {player.position === 'RB' && stats?.rushingYards! > 0 && (
                        <div className="flex justify-between text-sm">
                          <span className="text-gray-600">Rushing Yards</span>
                          <span className="text-gray-900">{stats?.rushingYards}</span>
                        </div>
                      )}
                      {(player.position === 'WR' || player.position === 'TE') && stats?.receivingYards! > 0 && (
                        <div className="flex justify-between text-sm">
                          <span className="text-gray-600">Receiving Yards</span>
                          <span className="text-gray-900">{stats?.receivingYards}</span>
                        </div>
                      )}
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-600">Total TDs</span>
                        <span className="text-gray-900">{stats?.totalTDs || 0}</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-600">
                          {player.position === 'QB' ? 'Passing Yards' : player.position === 'RB' ? 'Rushing Yards' : 'Receiving Yards'}
                        </span>
                        <span className="text-gray-900">
                          {player.position === 'QB' ? stats?.passingYards || 0 : player.position === 'RB' ? stats?.rushingYards || 0 : stats?.receivingYards || 0}
                        </span>
                      </div>
                      {permissions.statsDepth === 'full' && (
                        <>
                          <div className="pt-2 border-t mt-2">
                            <div className="text-xs text-gray-500 mb-1">Advanced Metrics</div>
                            <div className="flex justify-between text-xs">
                              <span className="text-gray-600">Yards/Attempt</span>
                              <span className="text-gray-900">
                                {player.position === 'QB' && stats?.passingYards
                                  ? (stats.passingYards / 30).toFixed(1)
                                  : player.position === 'RB' && stats?.rushingYards
                                  ? (stats.rushingYards / 15).toFixed(1)
                                  : '-'}
                              </span>
                            </div>
                          </div>
                        </>
                      )}
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
          {!loading && !error && filteredPlayers.length === 0 && (
            <div className="text-xs text-gray-500">No players found. Adjust your search.</div>
          )}
        </TabsContent>

        <TabsContent value="table" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Player Statistics</CardTitle>
              <CardDescription>Comprehensive player performance data</CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Player</TableHead>
                    <TableHead>Position</TableHead>
                    <TableHead>Team</TableHead>
                    <TableHead className="text-right">Pass Yds</TableHead>
                    <TableHead className="text-right">Rush Yds</TableHead>
                    <TableHead className="text-right">Rec Yds</TableHead>
                    <TableHead className="text-right">TDs</TableHead>
                    <TableHead className="text-right">Games</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredPlayers.map(player => (
                    <TableRow key={player.playerId ?? player.playerid ?? player.name}>
                      <TableCell className="text-gray-900">{player.name}</TableCell>
                      <TableCell>
                        <Badge variant="outline" className={positionColors[player.position] || ''}>
                          {player.position}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-gray-600">
                        {player.teamName}
                        {player.jerseyNumber && <span className="text-xs text-gray-500 ml-1">#{player.jerseyNumber}</span>}
                      </TableCell>
                      <TableCell className="text-right">{player.passingYards || '-'}</TableCell>
                      <TableCell className="text-right">{player.rushingYards || '-'}</TableCell>
                      <TableCell className="text-right">{player.receivingYards || '-'}</TableCell>
                      <TableCell className="text-right">{player.totalTDs}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {players.length === 0 && !loading && !error && (
        <Card>
          <CardContent className="py-12 text-center">
            <p className="text-gray-500">No players found matching your filters.</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
