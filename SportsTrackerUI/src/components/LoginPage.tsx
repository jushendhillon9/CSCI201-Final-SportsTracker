import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from './figma-ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './figma-ui/card';
import { Input } from './figma-ui/input';
import { Label } from './figma-ui/label';
import { type User } from '../App';
import { Shield, TrendingUp, Users, BarChart3 } from 'lucide-react';

const CLIENT_ID = '183700834954-uhqj0th2d6flunub0cr9gp625jct9fod.apps.googleusercontent.com';
const SCOPES = ['openid', 'email', 'profile'].join(' ');

interface LoginPageProps {
  onLogin: (user: User) => void;
}

export default function LoginPage({ onLogin }: LoginPageProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const tokenClientRef = useRef<any>(null);

  // Load GIS script once (using the working approach from first snippet)
  useEffect(() => {
    const id = 'google-gis';
    if (document.getElementById(id)) return;
    
    const s = document.createElement('script');
    s.id = id;
    s.src = 'https://accounts.google.com/gsi/client';
    s.async = true;
    s.defer = true;
    s.onload = () => {
      tokenClientRef.current = (window as any).google.accounts.oauth2.initTokenClient({
        client_id: CLIENT_ID,
        scope: SCOPES,
        callback: async (resp: any) => {
          if (!resp?.access_token) {
            console.error('No access token returned:', resp);
            setLoading(false);
            return;
          }
          try {
            const userInfoRes = await fetch(
              'https://www.googleapis.com/oauth2/v3/userinfo',
              {
                headers: {
                   Authorization: `Bearer ${resp.access_token}`,
                },
              }
            );
            if (!userInfoRes.ok) {
              throw new Error('Failed to fetch Google user info');
            }
            const userInfo = await userInfoRes.json();
            const user: User = {
              userid: Date.now(),
              email: userInfo.email,
              name: userInfo.name,
              role: 'user'
            };
            localStorage.setItem('accessToken', resp.access_token);
            onLogin(user);
            navigate('/dashboard');
          }
          catch (err) {
            console.error('Google login failed:', err);
            setLoading(false);
          }
        },
        error_callback: (err: any) => {
          console.error('Google sign-in failed:', err);
          setLoading(false);
        },
      });
    };
    document.body.appendChild(s);
  }, [onLogin, navigate]);

  const handleGoogleLogin = () => {
    if (!tokenClientRef.current) {
      console.error('Token client not initialized yet');
      alert('Google login is still loading, please try again in a second.');
      return;
    }
    setLoading(true);
    tokenClientRef.current.requestAccessToken();
  };

  const handleGuestAccess = () => {
    const guestUser: User = {
      userid: 0,
      email: 'guest@cfbtracker.com',
      role: 'guest'
    };
    onLogin(guestUser);
    navigate('/dashboard');
  };

  const handleEmailLogin = (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    setTimeout(() => {
      const user: User = {
        userid: 2,
        email: email,
        role: 'user',
        name: email.split('@')[0]
      };
      onLogin(user);
      navigate('/dashboard');
    }, 1000);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-red-50 flex flex-col">
      {/* Header */}
      <header className="p-6">
        <div className="max-w-7xl mx-auto flex items-center gap-3">
          <div className="bg-blue-600 text-white p-2 rounded-lg">
            <BarChart3 className="h-6 w-6" />
          </div>
          <span className="text-xl text-blue-900">College Football Stat Tracker</span>
        </div>
      </header>

      {/* Main Content */}
      <div className="flex-1 flex items-center justify-center p-6">
        <div className="max-w-6xl w-full grid md:grid-cols-2 gap-12 items-center">
          {/* Left Side - Marketing Content */}
          <div className="space-y-8">
            <div>
              <h1 className="text-4xl text-gray-900 mb-4">
                Real-Time NCAA Football Analytics
              </h1>
              <p className="text-xl text-gray-600">
                Track live stats, analyze player performance, and get data-driven projections for college football.
              </p>
            </div>

            <div className="space-y-4">
              <div className="flex items-start gap-4">
                <div className="bg-blue-100 p-3 rounded-lg">
                  <TrendingUp className="h-6 w-6 text-blue-600" />
                </div>
                <div>
                  <h3 className="text-gray-900 mb-1">Live Statistics</h3>
                  <p className="text-gray-600">Real-time game stats and player performance updates</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="bg-red-100 p-3 rounded-lg">
                  <Users className="h-6 w-6 text-red-600" />
                </div>
                <div>
                  <h3 className="text-gray-900 mb-1">Player Insights</h3>
                  <p className="text-gray-600">Detailed analytics for thousands of NCAA players</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="bg-green-100 p-3 rounded-lg">
                  <Shield className="h-6 w-6 text-green-600" />
                </div>
                <div>
                  <h3 className="text-gray-900 mb-1">Performance Projections</h3>
                  <p className="text-gray-600">AI-powered predictions based on historical data</p>
                </div>
              </div>
            </div>
          </div>

          {/* Right Side - Login Form */}
          <Card className="shadow-xl">
            <CardHeader>
              <CardTitle>Sign In</CardTitle>
              <CardDescription>
                Access full live data, projections, and historical seasons
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Google SSO Button */}
              <Button
                onClick={handleGoogleLogin}
                disabled={loading}
                className="w-full bg-white hover:bg-gray-50 text-gray-900 border border-gray-300"
                size="lg"
              >
                <svg className="h-5 w-5 mr-2" viewBox="0 0 24 24">
                  <path
                    fill="#4285F4"
                    d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  />
                  <path
                    fill="#34A853"
                    d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  />
                  <path
                    fill="#FBBC05"
                    d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                  />
                  <path
                    fill="#EA4335"
                    d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                  />
                </svg>
                {loading ? 'Signing in...' : 'Sign in with Google'}
              </Button>

              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <span className="w-full border-t" />
                </div>
                <div className="relative flex justify-center text-xs uppercase">
                  <span className="bg-white px-2 text-gray-500">Or continue with email</span>
                </div>
              </div>

              {/* Email/Password Form */}
              <form onSubmit={handleEmailLogin} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    placeholder="you@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="password">Password</Label>
                  <Input
                    id="password"
                    type="password"
                    placeholder="••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
                <Button type="submit" className="w-full" disabled={loading}>
                  Sign In
                </Button>
              </form>

              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <span className="w-full border-t" />
                </div>
                <div className="relative flex justify-center text-xs uppercase">
                  <span className="bg-white px-2 text-gray-500">Limited Access</span>
                </div>
              </div>

              {/* Guest Access */}
              <Button
                onClick={handleGuestAccess}
                variant="outline"
                className="w-full"
              >
                Continue as Guest
              </Button>

              <p className="text-xs text-gray-500 text-center">
                Guest access: Top 25 teams only, 10-minute delayed data
              </p>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}