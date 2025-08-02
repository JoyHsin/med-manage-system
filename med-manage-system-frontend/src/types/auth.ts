export interface LoginRequest {
  username: string;
  password: string;
}

export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phone?: string;
  hireDate?: string;
  employeeId?: string;
  department: string;
  position: string;
  enabled: boolean;
  accountNonExpired: boolean;
  accountNonLocked: boolean;
  credentialsNonExpired: boolean;
  lastLoginTime?: string;
  lastLoginIp?: string;
  passwordChangedTime?: string;
  failedLoginAttempts: number;
  lockedTime?: string;
  createdAt: string;
  updatedAt: string;
  roles: Role[];
}

export interface Role {
  id: number;
  name: string;
  code: string;
  description?: string;
  isSystemRole: boolean;
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateRoleRequest {
  roleName: string;
  roleCode: string;
  description?: string;
}

export interface UpdateRoleRequest {
  roleName?: string;
  description?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  userId: number;
  username: string;
  fullName: string;
  email: string;
  department: string;
  position: string;
  expiresIn: number;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message?: string;
  data?: T;
  total?: number;
  timestamp: number;
}

export interface CreateUserRequest {
  username: string;
  password: string;
  fullName: string;
  email: string;
  phone?: string;
  hireDate?: string;
  employeeId?: string;
  department: string;
  position: string;
  enabled?: boolean;
}

export interface UpdateUserRequest {
  fullName?: string;
  email?: string;
  phone?: string;
  hireDate?: string;
  employeeId?: string;
  department?: string;
  position?: string;
  enabled?: boolean;
}