import { type User } from '../App';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './figma-ui/card';
import { Badge } from './figma-ui/badge';
import { Progress } from './figma-ui/progress';
import { Alert, AlertDescription } from './figma-ui/alert';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './figma-ui/table';
import { TrendingUp, AlertCircle, Target, Zap, Activity } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';
import { getAllProjections, getModelMetrics, samplePlayerStats } from '../lib/projectionEngine';

interface ProjectionsPageProps {
  user: User;
}

const positionColors: Record<string, string> = {
  QB: 'bg-blue-100 text-blue-700 border-blue-300',
  RB: 'bg-green-100 text-green-700 border-green-300',
  WR: 'bg-purple-100 text-purple-700 border-purple-300',
  TE: 'bg-orange-100 text-orange-700 border-orange-300',
};

export default function ProjectionsPage({ user }: ProjectionsPageProps) {
  // Get real projections from our engine
  const projections = getAllProjections();
  const modelMetrics = getModelMetrics();

  // Generate chart data from actual stats
  const generateChartData = (playerId: number) => {
    const playerData = samplePlayerStats[playerId];
    if (!playerData) return [];
    
    // Reverse to show chronological order, then add projection
    const historicalData = playerData.stats.slice(0, 4).reverse().map((stat, index) => ({
      week: `Week ${8 + index}`,
      yards: stat.yards,
      tds: stat.tds,
      projected: false,
    }));

    // Add projected week
    const projection = projections.find(p => p.playerId === playerId);
    if (projection) {
      historicalData.push({
        week: 'Week 12 (Proj)',
        yards: projection.projectedYards,
        tds: projection.projectedTDs,
        projected: true,
      });
    }

    return historicalData;
  };

  const getConfidenceColor = (confidence: number) => {
    if (confidence >= 0.85) return 'text-green-600';
    if (confidence >= 0.70) return 'text-blue-600';
    return 'text-orange-600';
  };

  // Calculate average confidence
  const avgConfidence = projections.length > 0
    ? projections.reduce((sum, p) => sum + p.confidence, 0) / projections.length
    : 0;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 mb-2">Performance Projections</h1>
        <p className="text-gray-600">
          Statistical predictions based on recent game performance
        </p>
      </div>

      {/* Info Alert */}
      <Alert>
        <AlertCircle className="h-4 w-4" />
        <AlertDescription>
          Projections use a <strong>{modelMetrics.algorithm}</strong> analyzing {modelMetrics.dataPoints}. 
          Confidence is calculated using the {modelMetrics.confidenceMethod} - lower variance = higher confidence.
        </AlertDescription>
      </Alert>

      {/* Algorithm Overview */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Algorithm</CardTitle>
            <Zap className="h-4 w-4 text-yellow-600" />
          </CardHeader>
          <CardContent>
            <div className="text-xl mb-1">Moving Average</div>
            <p className="text-xs text-gray-500">
              3-game rolling window
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Data Window</CardTitle>
            <Target className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-xl mb-1">Last 3 Games</div>
            <p className="text-xs text-gray-500">
              Recent performance focus
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Avg Confidence</CardTitle>
            <TrendingUp className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-xl mb-1">
              {(avgConfidence * 100).toFixed(0)}%
            </div>
            <p className="text-xs text-gray-500">
              Across all projections
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Players Tracked</CardTitle>
            <Activity className="h-4 w-4 text-purple-600" />
          </CardHeader>
          <CardContent>
            <div className="text-xl mb-1">{projections.length}</div>
            <p className="text-xs text-gray-500">
              With upcoming games
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Projections Cards */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {projections.map(projection => {
          const chartData = generateChartData(projection.playerId);
          
          return (
            <Card key={projection.playerId}>
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <CardTitle className="text-lg mb-1">{projection.playerName}</CardTitle>
                    <CardDescription>{projection.opponent}</CardDescription>
                  </div>
                  <Badge variant="outline" className={positionColors[projection.position] || ''}>
                    {projection.position}
                  </Badge>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                {/* Projected Stats */}
                <div className="grid grid-cols-2 gap-4">
                  <div className="bg-blue-50 p-4 rounded-lg">
                    <div className="text-sm text-gray-600 mb-1">Projected Yards</div>
                    <div className="text-2xl text-blue-600">
                      {projection.projectedYards}
                    </div>
                  </div>
                  <div className="bg-green-50 p-4 rounded-lg">
                    <div className="text-sm text-gray-600 mb-1">Projected TDs</div>
                    <div className="text-2xl text-green-600">
                      {projection.projectedTDs}
                    </div>
                  </div>
                </div>

                {/* Confidence Score */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm text-gray-600">Confidence Score</span>
                    <span className={`text-sm font-medium ${getConfidenceColor(projection.confidence)}`}>
                      {projection.confidenceLevel} ({(projection.confidence * 100).toFixed(0)}%)
                    </span>
                  </div>
                  <Progress value={projection.confidence * 100} className="h-2" />
                </div>

                {/* Trend Chart */}
                <div>
                  <div className="text-sm text-gray-600 mb-3">Performance Trend</div>
                  <ResponsiveContainer width="100%" height={150}>
                    <LineChart data={chartData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="week" tick={{ fontSize: 10 }} />
                      <YAxis tick={{ fontSize: 10 }} />
                      <Tooltip />
                      <Line 
                        type="monotone" 
                        dataKey="yards" 
                        stroke="#2563eb" 
                        strokeWidth={2}
                        dot={(props: any) => {
                          const { cx, cy, payload } = props;
                          if (payload.projected) {
                            return <circle cx={cx} cy={cy} r={5} fill="#2563eb" stroke="#fff" strokeWidth={2} />;
                          }
                          return <circle cx={cx} cy={cy} r={3} fill="#2563eb" />;
                        }}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>

                {/* Recent Games Used */}
                <div className="pt-3 border-t">
                  <details className="cursor-pointer">
                    <summary className="text-sm text-gray-600 hover:text-gray-900">
                      View calculation details
                    </summary>
                    <div className="mt-3 text-xs space-y-2 bg-gray-50 p-3 rounded">
                      <div>
                        <strong>Method:</strong> {projection.method}
                      </div>
                      <div>
                        <strong>Formula:</strong> projection = avg(last 3 games)
                      </div>
                      <div>
                        <strong>Confidence:</strong> 1 - (stdDev / mean)
                      </div>
                      <div className="pt-2 border-t mt-2">
                        <strong>Games used:</strong>
                        <ul className="mt-1 space-y-1">
                          {projection.recentGames.map((game, i) => (
                            <li key={game.gameId}>
                              Game {i + 1}: {game.yards} yds, {game.tds} TDs ({game.date})
                            </li>
                          ))}
                        </ul>
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
          <CardDescription>Complete overview of player projections using {modelMetrics.algorithm}</CardDescription>
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
              {projections.map(projection => (
                <TableRow key={projection.playerId}>
                  <TableCell className="font-medium">{projection.playerName}</TableCell>
                  <TableCell>
                    <Badge variant="outline" className={positionColors[projection.position] || ''}>
                      {projection.position}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-gray-600">{projection.opponent}</TableCell>
                  <TableCell className="text-right text-blue-600 font-medium">
                    {projection.projectedYards}
                  </TableCell>
                  <TableCell className="text-right text-green-600 font-medium">
                    {projection.projectedTDs}
                  </TableCell>
                  <TableCell className="text-right">
                    <span className={getConfidenceColor(projection.confidence)}>
                      {(projection.confidence * 100).toFixed(0)}%
                    </span>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Model Info */}
      <Card>
        <CardHeader>
          <CardTitle>Model Information</CardTitle>
          <CardDescription>Technical details about the projection algorithm</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-gray-600">Algorithm:</span>
                <span className="font-medium">{modelMetrics.algorithm}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Data Window:</span>
                <span className="font-medium">{modelMetrics.dataPoints}</span>
              </div>
            </div>
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-gray-600">Confidence Method:</span>
                <span className="font-medium">{modelMetrics.confidenceMethod}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Expected Accuracy:</span>
                <span className="font-medium">{modelMetrics.accuracy}</span>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
