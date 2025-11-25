import { useState } from 'react';
import { type User } from '../App';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './figma-ui/card';
import { Input } from './figma-ui/input';
import { Badge } from './figma-ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './figma-ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './figma-ui/table';
import { Search, Trophy, TrendingUp, TrendingDown } from 'lucide-react';
import { mockTeams } from '../lib/mockData';

interface TeamsPageProps {
  user: User;
}

export default function TeamsPage({ user }: TeamsPageProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [conferenceFilter, setConferenceFilter] = useState<string>('all');
  
  const isGuest = user.role === 'guest';
  const allTeams = isGuest ? mockTeams.slice(0, 25) : mockTeams;

  const conferences = ['all', ...new Set(mockTeams.map(t => t.conference))];

  const filteredTeams = allTeams.filter(team => {
    const matchesSearch = team.name.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesConference = conferenceFilter === 'all' || team.conference === conferenceFilter;
    return matchesSearch && matchesConference;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 mb-2">Teams</h1>
        <p className="text-gray-600">
          Browse and analyze college football teams {isGuest && '(Top 25 only)'}
        </p>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                placeholder="Search teams..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Select value={conferenceFilter} onValueChange={setConferenceFilter}>
              <SelectTrigger className="w-full md:w-[200px]">
                <SelectValue placeholder="Conference" />
              </SelectTrigger>
              <SelectContent>
                {conferences.map(conf => (
                  <SelectItem key={conf} value={conf}>
                    {conf === 'all' ? 'All Conferences' : conf}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Teams Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredTeams.map(team => (
          <Card key={team.teamid} className="hover:shadow-lg transition-shadow">
            <CardHeader>
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    {team.ranking && (
                      <Badge className="bg-blue-600">#{team.ranking}</Badge>
                    )}
                    <CardTitle className="text-lg">{team.name}</CardTitle>
                  </div>
                  <CardDescription>{team.conference}</CardDescription>
                </div>
                <div className="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center">
                  <Trophy className="h-6 w-6 text-gray-400" />
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">Record</span>
                  <span className="text-gray-900">
                    {team.wins}-{team.losses}
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">Win Rate</span>
                  <div className="flex items-center gap-2">
                    <span className="text-gray-900">
                      {team.wins && team.losses ? 
                        `${((team.wins / (team.wins + team.losses)) * 100).toFixed(0)}%` 
                        : '0%'}
                    </span>
                    {team.wins! > team.losses! ? (
                      <TrendingUp className="h-4 w-4 text-green-600" />
                    ) : (
                      <TrendingDown className="h-4 w-4 text-red-600" />
                    )}
                  </div>
                </div>
                <div className="pt-2 border-t">
                  <div className="grid grid-cols-2 gap-2 text-center">
                    <div>
                      <div className="text-2xl text-green-600">{team.wins}</div>
                      <div className="text-xs text-gray-500">Wins</div>
                    </div>
                    <div>
                      <div className="text-2xl text-red-600">{team.losses}</div>
                      <div className="text-xs text-gray-500">Losses</div>
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Teams Table */}
      <Card>
        <CardHeader>
          <CardTitle>Rankings Table</CardTitle>
          <CardDescription>Complete team standings</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Rank</TableHead>
                <TableHead>Team</TableHead>
                <TableHead>Conference</TableHead>
                <TableHead className="text-right">Wins</TableHead>
                <TableHead className="text-right">Losses</TableHead>
                <TableHead className="text-right">Win %</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredTeams.map(team => (
                <TableRow key={team.teamid}>
                  <TableCell>
                    {team.ranking && (
                      <Badge variant="outline">#{team.ranking}</Badge>
                    )}
                  </TableCell>
                  <TableCell className="text-gray-900">{team.name}</TableCell>
                  <TableCell className="text-gray-600">{team.conference}</TableCell>
                  <TableCell className="text-right text-green-600">{team.wins}</TableCell>
                  <TableCell className="text-right text-red-600">{team.losses}</TableCell>
                  <TableCell className="text-right">
                    {team.wins && team.losses ? 
                      `${((team.wins / (team.wins + team.losses)) * 100).toFixed(1)}%` 
                      : '0%'}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {filteredTeams.length === 0 && (
        <Card>
          <CardContent className="py-12 text-center">
            <p className="text-gray-500">No teams found matching your filters.</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
