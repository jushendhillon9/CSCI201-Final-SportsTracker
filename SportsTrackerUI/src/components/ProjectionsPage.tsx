import { type User } from '../App';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './figma-ui/card';
import { Badge } from './figma-ui/badge';
import { Progress } from './figma-ui/progress';
import { Alert, AlertDescription } from './figma-ui/alert';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './figma-ui/table';
import { TrendingUp, AlertCircle, Target, Zap } from 'lucide-react';
import { mockProjections, mockPlayers } from '../lib/mockData';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';

interface ProjectionsPageProps {
  user: User;
}

export default function ProjectionsPage({ user }: ProjectionsPageProps) {
  // Mock historical data for trend chart
  const generateHistoricalData = (projection: typeof mockProjections[0]) => {
    return [
      { week: 'Week 8', yards: projection.projected_yards * 0.85, tds: projection.projected_tds * 0.8 },
      { week: 'Week 9', yards: projection.projected_yards * 0.95, tds: projection.projected_tds * 0.9 },
      { week: 'Week 10', yards: projection.projected_yards * 1.1, tds: projection.projected_tds * 1.1 },
      { week: 'Week 11', yards: projection.projected_yards * 0.9, tds: projection.projected_tds * 0.95 },
      { week: 'Week 12 (Proj)', yards: projection.projected_yards, tds: projection.projected_tds, projected: true },
    ];
  };

  const getConfidenceColor = (confidence: number) => {
    if (confidence >= 0.85) return 'text-green-600';
    if (confidence >= 0.75) return 'text-blue-600';
    return 'text-orange-600';
  };

  const getConfidenceLabel = (confidence: number) => {
    if (confidence >= 0.85) return 'High';
    if (confidence >= 0.75) return 'Medium';
    return 'Moderate';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 mb-2">Performance Projections</h1>
        <p className="text-gray-600">
          AI-powered predictions based on historical performance and opponent analysis
        </p>
      </div>

      {/* Info Alert */}
      <Alert>
        <AlertCircle className="h-4 w-4" />
        <AlertDescription>
          Projections are calculated using a rolling average of the last 3 games, adjusted for opponent defensive strength. 
          Confidence scores reflect statistical variance and data quality.
        </AlertDescription>
      </Alert>

      {/* Algorithm Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Algorithm</CardTitle>
            <Zap className="h-4 w-4 text-yellow-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl mb-1">Linear Model</div>
            <p className="text-xs text-gray-500">
              Rolling 3-game average with opponent adjustments
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Data Points</CardTitle>
            <Target className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl mb-1">Last 3 Games</div>
            <p className="text-xs text-gray-500">
              Recent performance window for accuracy
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Avg Confidence</CardTitle>
            <TrendingUp className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl mb-1">
              {(mockProjections.reduce((sum, p) => sum + p.confidence, 0) / mockProjections.length * 100).toFixed(0)}%
            </div>
            <p className="text-xs text-gray-500">
              Across all projections
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Projections Cards */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {mockProjections.map(projection => {
          const player = mockPlayers.find(p => p.playerid === projection.playerid);
          const historicalData = generateHistoricalData(projection);
          
          return (
            <Card key={projection.proj_id}>
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <CardTitle className="text-lg mb-1">{projection.player_name}</CardTitle>
                    <CardDescription>{projection.opponent}</CardDescription>
                  </div>
                  <Badge variant="outline" className={positionColors[player?.position || 'QB']}>
                    {player?.position}
                  </Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Projected Stats */}
                <div className="grid grid-cols-2 gap-4">
                  <div className="bg-blue-50 p-4 rounded-lg">
                    <div className="text-sm text-gray-600 mb-1">Projected Yards</div>
                    <div className="text-2xl text-blue-600">
                      {projection.projected_yards.toFixed(0)}
                    </div>
                  </div>
                  <div className="bg-green-50 p-4 rounded-lg">
                    <div className="text-sm text-gray-600 mb-1">Projected TDs</div>
                    <div className="text-2xl text-green-600">
                      {projection.projected_tds.toFixed(1)}
                    </div>
                  </div>
                </div>

                {/* Confidence Score */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm text-gray-600">Confidence Score</span>
                    <span className={`text-sm ${getConfidenceColor(projection.confidence)}`}>
                      {getConfidenceLabel(projection.confidence)} ({(projection.confidence * 100).toFixed(0)}%)
                    </span>
                  </div>
                  <Progress value={projection.confidence * 100} className="h-2" />
                </div>

                {/* Trend Chart */}
                <div>
                  <div className="text-sm text-gray-600 mb-3">Performance Trend</div>
                  <ResponsiveContainer width="100%" height={150}>
                    <LineChart data={historicalData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="week" tick={{ fontSize: 10 }} />
                      <YAxis tick={{ fontSize: 10 }} />
                      <Tooltip />
                      <Line 
                        type="monotone" 
                        dataKey="yards" 
                        stroke="#2563eb" 
                        strokeWidth={2}
                        strokeDasharray={historicalData[historicalData.length - 1].projected ? "5 5" : ""}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>

                {/* Formula Info */}
                <div className="pt-3 border-t">
                  <details className="cursor-pointer">
                    <summary className="text-sm text-gray-600 hover:text-gray-900">
                      View calculation details
                    </summary>
                    <div className="mt-3 text-xs text-gray-500 space-y-2 bg-gray-50 p-3 rounded">
                      <div>
                        <strong>Formula:</strong> pred = w1 × avg_yards_3 + w2 × defensive_adj + b
                      </div>
                      <div>
                        <strong>Confidence:</strong> 1 - variance(last_N_games)
                      </div>
                      <div>
                        <strong>Data window:</strong> Last 3 games
                      </div>
                    </div>
                  </details>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      {/* Projections Table */}
      <Card>
        <CardHeader>
          <CardTitle>All Projections Summary</CardTitle>
          <CardDescription>Complete overview of player projections</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Player</TableHead>
                <TableHead>Position</TableHead>
                <TableHead>Opponent</TableHead>
                <TableHead className="text-right">Proj. Yards</TableHead>
                <TableHead className="text-right">Proj. TDs</TableHead>
                <TableHead className="text-right">Confidence</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {mockProjections.map(projection => {
                const player = mockPlayers.find(p => p.playerid === projection.playerid);
                return (
                  <TableRow key={projection.proj_id}>
                    <TableCell className="text-gray-900">{projection.player_name}</TableCell>
                    <TableCell>
                      <Badge variant="outline" className={positionColors[player?.position || 'QB']}>
                        {player?.position}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-gray-600">{projection.opponent}</TableCell>
                    <TableCell className="text-right text-blue-600">
                      {projection.projected_yards.toFixed(0)}
                    </TableCell>
                    <TableCell className="text-right text-green-600">
                      {projection.projected_tds.toFixed(1)}
                    </TableCell>
                    <TableCell className="text-right">
                      <span className={getConfidenceColor(projection.confidence)}>
                        {(projection.confidence * 100).toFixed(0)}%
                      </span>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}

const positionColors: Record<string, string> = {
  QB: 'bg-blue-100 text-blue-700 border-blue-300',
  RB: 'bg-green-100 text-green-700 border-green-300',
  WR: 'bg-purple-100 text-purple-700 border-purple-300',
  TE: 'bg-orange-100 text-orange-700 border-orange-300',
};
