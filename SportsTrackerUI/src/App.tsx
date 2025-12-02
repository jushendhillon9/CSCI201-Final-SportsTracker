import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import LoginPage from './components/LoginPage';
import Dashboard from './components/Dashboard';
import TeamsPage from './components/TeamsPage';
import PlayersPage from './components/PlayersPage';
import GamesPage from './components/GamesPage';
import ProjectionsPage from './components/ProjectionsPage';
import ErrorPage from './components/ErrorPage';
import Layout from './components/Layout';

export interface User {
  userid: number;
  email: string;
  role: 'guest' | 'user' | 'admin';
  name?: string;
}

function App() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check for existing session
    const savedUser = localStorage.getItem('cfb_user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const handleLogin = (userData: User) => {
    setUser(userData);
    localStorage.setItem('cfb_user', JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('cfb_user');
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <Router>
      <Routes>
        <Route path="/login" element={
          user ? <Navigate to="/dashboard" /> : <LoginPage onLogin={handleLogin} />
        } />
        
        <Route path="/" element={
          user ? <Layout user={user} onLogout={handleLogout} /> : <Navigate to="/login" />
        }>
          <Route index element={<Navigate to="/dashboard" />} />
          <Route path="dashboard" element={<Dashboard user={user!} />} />
          <Route path="teams" element={<TeamsPage user={user!} />} />
          <Route path="players" element={<PlayersPage user={user!} />} />
          <Route path="games" element={<GamesPage user={user!} />} />
          <Route path="projections" element={<ProjectionsPage user={user!} />} />
        </Route>

        <Route path="/error" element={<ErrorPage />} />
        <Route path="*" element={<ErrorPage type="404" />} />
      </Routes>
    </Router>
  );
}

export default App;