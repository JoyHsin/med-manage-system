import { apiClient } from './apiClient';
import type { User, CreateUserRequest, UpdateUserRequest, ApiResponse, Role } from '../types/auth';

class UserService {
  async getAllUsers(params?: {
    enabled?: boolean;
    department?: string;
    keyword?: string;
  }): Promise<ApiResponse<User[]>> {
    const response = await apiClient.get<ApiResponse<User[]>>('/users', { params });
    return response.data;
  }

  async getUserById(userId: number): Promise<ApiResponse<User>> {
    const response = await apiClient.get<ApiResponse<User>>(`/users/${userId}`);
    return response.data;
  }

  async getUserByUsername(username: string): Promise<ApiResponse<User>> {
    const response = await apiClient.get<ApiResponse<User>>(`/users/username/${username}`);
    return response.data;
  }

  async createUser(userData: CreateUserRequest): Promise<ApiResponse<User>> {
    const response = await apiClient.post<ApiResponse<User>>('/users', userData);
    return response.data;
  }

  async updateUser(userId: number, userData: UpdateUserRequest): Promise<ApiResponse<User>> {
    const response = await apiClient.put<ApiResponse<User>>(`/users/${userId}`, userData);
    return response.data;
  }

  async enableUser(userId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.put<ApiResponse<any>>(`/users/${userId}/enable`);
    return response.data;
  }

  async disableUser(userId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.put<ApiResponse<any>>(`/users/${userId}/disable`);
    return response.data;
  }

  async deleteUser(userId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.delete<ApiResponse<any>>(`/users/${userId}`);
    return response.data;
  }

  async assignRoleToUser(userId: number, roleId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/users/${userId}/roles/${roleId}`);
    return response.data;
  }

  async removeRoleFromUser(userId: number, roleId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.delete<ApiResponse<any>>(`/users/${userId}/roles/${roleId}`);
    return response.data;
  }

  async resetUserPassword(userId: number, newPassword: string): Promise<ApiResponse<any>> {
    const response = await apiClient.put<ApiResponse<any>>(`/users/${userId}/reset-password`, {
      newPassword
    });
    return response.data;
  }

  async checkUsernameExists(username: string): Promise<ApiResponse<{ exists: boolean }>> {
    const response = await apiClient.get<ApiResponse<{ exists: boolean }>>(`/users/check-username/${username}`);
    return response.data;
  }

  async checkEmailExists(email: string): Promise<ApiResponse<{ exists: boolean }>> {
    const response = await apiClient.get<ApiResponse<{ exists: boolean }>>(`/users/check-email/${email}`);
    return response.data;
  }

  // 角色相关接口
  async getAllRoles(): Promise<ApiResponse<Role[]>> {
    const response = await apiClient.get<ApiResponse<Role[]>>('/roles');
    return response.data;
  }

  async getEnabledRoles(): Promise<ApiResponse<Role[]>> {
    const response = await apiClient.get<ApiResponse<Role[]>>('/roles/enabled');
    return response.data;
  }

  async getUserRoles(userId: number): Promise<ApiResponse<Role[]>> {
    const response = await apiClient.get<ApiResponse<Role[]>>(`/roles/users/${userId}`);
    return response.data;
  }
}

export const userService = new UserService();