import { apiClient } from './apiClient';
import type { Role, ApiResponse, CreateRoleRequest, UpdateRoleRequest } from '../types/auth';

class RoleService {
  async getAllRoles(params?: { keyword?: string }): Promise<ApiResponse<Role[]>> {
    const response = await apiClient.get<ApiResponse<Role[]>>('/roles', { params });
    return response.data;
  }

  async getRoleById(roleId: number): Promise<ApiResponse<Role>> {
    const response = await apiClient.get<ApiResponse<Role>>(`/roles/${roleId}`);
    return response.data;
  }

  async createRole(roleData: CreateRoleRequest): Promise<ApiResponse<Role>> {
    const response = await apiClient.post<ApiResponse<Role>>('/roles', roleData);
    return response.data;
  }

  async updateRole(roleId: number, roleData: UpdateRoleRequest): Promise<ApiResponse<Role>> {
    const response = await apiClient.put<ApiResponse<Role>>(`/roles/${roleId}`, roleData);
    return response.data;
  }

  async deleteRole(roleId: number): Promise<ApiResponse<any>> {
    const response = await apiClient.delete<ApiResponse<any>>(`/roles/${roleId}`);
    return response.data;
  }

  async getEnabledRoles(): Promise<ApiResponse<Role[]>> {
    const response = await apiClient.get<ApiResponse<Role[]>>('/roles/enabled');
    return response.data;
  }

  async getRolePermissions(roleId: number): Promise<ApiResponse<any[]>> {
    const response = await apiClient.get<ApiResponse<any[]>>(`/roles/${roleId}/permissions`);
    return response.data;
  }

  async assignPermissionsToRole(roleId: number, permissionIds: number[]): Promise<ApiResponse<any>> {
    const response = await apiClient.post<ApiResponse<any>>(`/roles/${roleId}/permissions`, { permissionIds });
    return response.data;
  }

  async removePermissionsFromRole(roleId: number, permissionIds: number[]): Promise<ApiResponse<any>> {
    const response = await apiClient.delete<ApiResponse<any>>(`/roles/${roleId}/permissions`, { data: { permissionIds } });
    return response.data;
  }
}

export const roleService = new RoleService();