import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { type User } from '../App';
import { Button } from './figma-ui/button';
import { Avatar, AvatarFallback } from './figma-ui/avatar';
import { Badge } from './figma-ui/badge';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from './figma-ui/dropdown-menu';
import { BarChart3, Home, Users, UserCircle, Calendar, TrendingUp, LogOut } from 'lucide-react';
import { canAccessProjections, getUserPermissions } from '../lib/userPermissions';

interface LayoutProps {
  user: User;
  onLogout: () => void;
}

function GuestWarningBanner({ user, onSignIn }: { user: User; onSignIn: () => void }) {
  const permissions = getUserPermissions(user);
  return (
    <div className="mb-6 bg-orange-50 border border-orange-200 rounded-lg p-4">
      <div className="flex items-start gap-3">
        <div className="text-orange-600 mt-0.5">⚠️</div>
        <div className="flex-1">
          <h3 className="text-orange-900 mb-1">Guest Access Limited</h3>
          <p className="text-sm text-orange-700">
            You're viewing Top {permissions.teamsVisible} teams with {permissions.realtimeDelayMinutes}-minute delayed data. 
            Sign in for full access to all teams, live data, projections, and odds.
          </p>
          <Button
            size="sm"
            onClick={onSignIn}
            className="mt-3 bg-orange-600 hover:bg-orange-700"
          >
            Sign In Now
          </Button>
        </div>
      </div>
    </div>
  );
}

export default function Layout({ user, onLogout }: LayoutProps) {
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    onLogout();
    navigate('/login');
  };

  const navItems = [
    { path: '/dashboard', label: 'Dashboard', icon: Home },
    { path: '/teams', label: 'Teams', icon: Users },
    { path: '/players', label: 'Players', icon: UserCircle },
    { path: '/games', label: 'Games', icon: Calendar },
    { path: '/projections', label: 'Projections', icon: TrendingUp, requiresAuth: true },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            {/* Logo */}
            <Link to="/dashboard" className="flex items-center gap-3">
              <div className="bg-blue-600 text-white p-2 rounded-lg">
                <BarChart3 className="h-5 w-5" />
              </div>
              <span className="text-lg text-gray-900">CFB Stat Tracker</span>
            </Link>

            {/* Navigation */}
            <nav className="hidden md:flex items-center gap-1">
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive = location.pathname === item.path;
                const isDisabled = item.requiresAuth && !canAccessProjections(user);

                return (
                  <Link
                    key={item.path}
                    to={isDisabled ? '#' : item.path}
                    className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-colors ${
                      isActive
                        ? 'bg-blue-50 text-blue-600'
                        : isDisabled
                        ? 'text-gray-400 cursor-not-allowed'
                        : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
                    }`}
                    onClick={(e) => isDisabled && e.preventDefault()}
                  >
                    <Icon className="h-4 w-4" />
                    {item.label}
                  </Link>
                );
              })}
            </nav>

            {/* User Menu */}
            <div className="flex items-center gap-3">
              {user.role === 'guest' && (
                <Badge variant="outline" className="text-orange-600 border-orange-300">
                  Guest Mode
                </Badge>
              )}
              
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="relative h-10 w-10 rounded-full">
                    <Avatar>
                      <AvatarFallback className="bg-blue-600 text-white">
                        {user.name ? user.name[0].toUpperCase() : user.email[0].toUpperCase()}
                      </AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <DropdownMenuLabel>
                    <div className="flex flex-col space-y-1">
                      <p className="text-sm">{user.name || 'User'}</p>
                      <p className="text-xs text-gray-500">{user.email}</p>
                      <Badge className="w-fit mt-1" variant={user.role === 'guest' ? 'outline' : 'default'}>
                        {user.role.charAt(0).toUpperCase() + user.role.slice(1)}
                      </Badge>
                    </div>
                  </DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={handleLogout}>
                    <LogOut className="mr-2 h-4 w-4" />
                    <span>Log out</span>
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </div>
        </div>

        {/* Mobile Navigation */}
        <div className="md:hidden border-t border-gray-200">
          <nav className="flex overflow-x-auto">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              const isDisabled = item.requiresAuth && !canAccessProjections(user);

              return (
                <Link
                  key={item.path}
                  to={isDisabled ? '#' : item.path}
                  className={`flex flex-col items-center gap-1 px-4 py-3 min-w-fit ${
                    isActive
                      ? 'text-blue-600 border-b-2 border-blue-600'
                      : isDisabled
                      ? 'text-gray-400'
                      : 'text-gray-600'
                  }`}
                  onClick={(e) => isDisabled && e.preventDefault()}
                >
                  <Icon className="h-5 w-5" />
                  <span className="text-xs">{item.label}</span>
                </Link>
              );
            })}
          </nav>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {user.role === 'guest' && (
          <GuestWarningBanner user={user} onSignIn={handleLogout} />
        )}
        <Outlet />
      </main>
    </div>
  );
}
