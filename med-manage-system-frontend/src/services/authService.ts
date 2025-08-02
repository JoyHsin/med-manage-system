import { apiClient } from './apiClient';
import type { LoginRequest, AuthResponse, ApiResponse } from '../types/auth';

class AuthService {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  }

  async logout(): Promise<void> {
    await apiClient.post('/auth/logout');
  }

  async validateToken(): Promise<boolean> {
    try {
      const response = await apiClient.get<{ valid: boolean }>('/auth/validate');
      return response.data.valid;
    } catch (error) {
      return false;
    }
  }

  async refreshToken(): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>('/auth/refresh');
    return response.data;
  }

  async getSessionInfo(): Promise<{ remainingMinutes: number; isValid: boolean }> {
    const response = await apiClient.get<ApiResponse<{ remainingMinutes: number; isValid: boolean }>>('/auth/session-info');
    return response.data.data!;
  }

  async getCurrentUser(): Promise<any> {
    const response = await apiClient.get('/auth/me');
    return response.data;
  }
}

export const authService = new AuthService();