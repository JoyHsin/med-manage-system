import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { message } from 'antd';
import { authService } from '../services/authService';
import type { User, LoginRequest } from '../types/auth';

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
        // 根据后端返回的数据构造user对象
        const userData: User = {
          id: response.userId,
          username: response.username,
          fullName: response.fullName,
          email: response.email,
          department: response.department,
          position: response.position,
          enabled: true,
          accountNonExpired: true,
          accountNonLocked: true,
          credentialsNonExpired: true,
          failedLoginAttempts: 0,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          roles: []
        };
        setUser(userData);
        localStorage.setItem('token', response.token);
        message.success('登录成功');
        return true;
      }
      return false;
    } catch (error: any) {
      console.error('Login error:', error);
      const errorMessage = error.response?.data?.message || '登录失败，请检查用户名和密码';
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