import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { message } from 'antd';
import { authService } from '../services/authService';
import type { User, LoginRequest } from '../types/auth';
import type { ApiError } from '../types/common';

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (credentials: LoginRequest) => Promise<boolean>;
  logout: () => void;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    try {
      const savedToken = localStorage.getItem('token');
      if (savedToken) {
        setToken(savedToken);
        // 验证token是否还有效
        authService.validateToken().then(isValid => {
          if (!isValid) {
            // Token无效，清除存储
            localStorage.removeItem('token');
            setToken(null);
          }
        }).catch(() => {
          // 验证失败，清除存储
          localStorage.removeItem('token');
          setToken(null);
        });
      }
    } catch (error) {
      console.error('Error accessing localStorage:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  const login = async (credentials: LoginRequest): Promise<boolean> => {
    try {
      setLoading(true);
      const response = await authService.login(credentials);
      
      if (response.token) {
        setToken(response.token);
        
        // 从后端获取完整的用户信息，避免硬编码
        try {
          const userInfo = await authService.getCurrentUser();
          setUser(userInfo);
        } catch (userError) {
          // 如果获取用户信息失败，使用响应中的基本信息
          console.warn('Failed to fetch user details, using basic info from login response');
          const userData: User = {
            id: response.userId,
            username: response.username,
            fullName: response.fullName,
            email: response.email,
            department: response.department,
            position: response.position,
            // 从后端响应获取，不硬编码
            enabled: true, // 默认值，应该从后端获取
            accountNonExpired: true,
            accountNonLocked: true,
            credentialsNonExpired: true,
            failedLoginAttempts: 0,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
            roles: [] // 应该从后端获取角色信息
          };
          setUser(userData);
        }
        
        localStorage.setItem('token', response.token);
        message.success('登录成功');
        return true;
      }
      return false;
    } catch (error) {
      console.error('Login error:', error);
      const apiError = error as ApiError;
      const errorMessage = apiError?.response?.data?.message || '登录失败，请检查用户名和密码';
      message.error(errorMessage);
      return false;
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      setUser(null);
      setToken(null);
      try {
        localStorage.removeItem('token');
      } catch (error) {
        console.error('Error removing token from localStorage:', error);
      }
      message.info('已退出登录');
    }
  };

  const value: AuthContextType = {
    user,
    token,
    login,
    logout,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};