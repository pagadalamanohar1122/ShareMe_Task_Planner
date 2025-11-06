import axios from 'axios';
import type { 
  AuthResponse, 
  LoginRequest, 
  SignupRequest, 
  ForgotPasswordRequest, 
  ResetPasswordRequest,
  User 
} from '../types/auth';

export interface Project {
  id: number;
  name: string;
  description?: string;
  status: 'ACTIVE' | 'COMPLETED' | 'ON_HOLD' | 'ARCHIVED';
  owner: User;
  members?: User[];
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  createdAt: string;
  updatedAt?: string;
  deadline?: string;
}

export interface CreateProjectRequest {
  name: string;
  description?: string;
  deadline?: string;
}

export interface DashboardStats {
  totalProjects: number;
  completedTasks: number;
  inProgressTasks: number;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests if available
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle token expiry
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Only redirect to login on 401 for authenticated endpoints, not for login itself
    if (error.response?.status === 401 && !error.config?.url?.includes('/api/auth/login')) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  async signup(data: SignupRequest): Promise<void> {
    await api.post('/api/auth/signup', data);
  },

  async login(data: LoginRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/api/auth/login', data);
    return response.data;
  },

  async getCurrentUser(): Promise<User> {
    const response = await api.get<User>('/api/auth/me');
    return response.data;
  },

  async forgotPassword(data: ForgotPasswordRequest): Promise<void> {
    await api.post('/api/auth/forgot', data);
  },

  async resetPassword(data: ResetPasswordRequest): Promise<void> {
    await api.post('/api/auth/reset', data);
  },
};

export const projectService = {
  async getProjects(): Promise<Project[]> {
    const response = await api.get<Project[]>('/api/projects');
    return response.data;
  },

  async createProject(data: CreateProjectRequest): Promise<Project> {
    const response = await api.post<Project>('/api/projects', data);
    return response.data;
  },

  async getProject(id: number): Promise<Project> {
    const response = await api.get<Project>(`/api/projects/${id}`);
    return response.data;
  },

  async updateProject(id: number, data: CreateProjectRequest): Promise<Project> {
    const response = await api.put<Project>(`/api/projects/${id}`, data);
    return response.data;
  },

  async deleteProject(id: number): Promise<void> {
    await api.delete(`/api/projects/${id}`);
  },

  async getStats(): Promise<DashboardStats> {
    const response = await api.get<DashboardStats>('/api/projects/stats');
    return response.data;
  },
};

export default api;