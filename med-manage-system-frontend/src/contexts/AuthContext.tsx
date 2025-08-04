import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { message } from 'antd';
import { authService } from '../services/authService';
import type { User, LoginRequest } from '../types/auth';
import type { ApiError } from '../types/common';
import { cleanToken } from '../utils/tokenUtils';

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (credentials: LoginRequest) => Promise<boolean>;
  logout: () => void;
  loading: boolean;
  fetchUserInfo: () => Promise<void>;
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
    const initializeAuth = async () => {
      try {
        const savedToken = localStorage.getItem('token');
        console.log('初始化认证，从localStorage获取token:', savedToken ? `${savedToken.substring(0, 20)}...` : 'null');
        
        if (savedToken) {
          // 确保令牌格式正确，移除可能存在的 Bearer 前缀
          const cleanedToken = cleanToken(savedToken);
          console.log('清理后的token:', cleanedToken ? `${cleanedToken.substring(0, 20)}...` : 'null');
          
          if (cleanedToken !== savedToken) {
            // 如果令牌格式不正确，更新存储
            localStorage.setItem('token', cleanedToken || '');
            console.log('更新localStorage中的token格式');
          }
          
          setToken(cleanedToken);
          
          // 验证token是否还有效
          try {
            const isValid = await authService.validateToken();
            if (!isValid) {
              console.log('Token验证失败，清除存储');
              localStorage.removeItem('token');
              setToken(null);
            } else {
              console.log('Token验证成功');
              // 注意：这里不立即获取用户信息，避免在初始化时就调用/auth/me
              // 用户信息将在实际需要时获取
            }
          } catch (error) {
            console.log('Token验证异常，清除存储:', error);
            localStorage.removeItem('token');
            setToken(null);
          }
        } else {
          console.log('未找到保存的token');
        }
      } catch (error) {
        console.error('初始化认证时出错:', error);
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();
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
        
        // 确保存储的令牌不包含 Bearer 前缀，由 apiClient 统一添加
        const cleanedToken = cleanToken(response.token);
        localStorage.setItem('token', cleanedToken || '');
        
        // 调试日志
        console.log('登录成功，保存token:', cleanedToken?.substring(0, 20) + '...');
        console.log('localStorage中的token:', localStorage.getItem('token')?.substring(0, 20) + '...');
        
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

  const fetchUserInfo = async () => {
    try {
      if (!token) {
        console.warn('没有token，无法获取用户信息');
        return;
      }
      
      console.log('开始获取用户信息...');
      const userInfo = await authService.getCurrentUser();
      setUser(userInfo);
      console.log('获取用户信息成功:', userInfo.username);
    } catch (error) {
      console.error('获取用户信息失败:', error);
      // 如果获取用户信息失败，可能是token无效，清除认证状态
      if ((error as any)?.response?.status === 401) {
        console.log('Token无效，清除认证状态');
        setToken(null);
        setUser(null);
        localStorage.removeItem('token');
      }
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
    fetchUserInfo,
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