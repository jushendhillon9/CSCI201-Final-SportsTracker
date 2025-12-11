const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080';

async function get<T>(path: string): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`);
  if (!res.ok) {
    throw new Error(`Request failed: ${res.status} ${res.statusText}`);
  }
  return res.json();
}

export const api = {
  getTeams: (params?: { conference?: string; search?: string }) => {
    const qs = new URLSearchParams();
    if (params?.conference && params.conference !== 'all') qs.set('conference', params.conference);
    if (params?.search) qs.set('search', params.search);
    const suffix = qs.toString() ? `?${qs.toString()}` : '';
    return get<any[]>(`/api/teams${suffix}`);
  },
  getGames: (params?: { season?: string; week?: string; teamId?: string; status?: string }) => {
    const qs = new URLSearchParams();
    if (params?.season) qs.set('season', params.season);
    if (params?.week) qs.set('week', params.week);
    if (params?.teamId) qs.set('teamId', params.teamId);
    if (params?.status && params.status !== 'all') qs.set('status', params.status);
    const suffix = qs.toString() ? `?${qs.toString()}` : '';
    return get<any[]>(`/api/games${suffix}`);
  },
  getPlayers: (params?: { teamId?: string; position?: string; search?: string }) => {
    const qs = new URLSearchParams();
    if (params?.teamId) qs.set('teamId', params.teamId);
    if (params?.position && params.position !== 'all') qs.set('position', params.position);
    if (params?.search) qs.set('search', params.search);
    const suffix = qs.toString() ? `?${qs.toString()}` : '';
    return get<any[]>(`/api/players${suffix}`);
  },
  getDashboard: () => get<any>('/api/dashboard'),
};
