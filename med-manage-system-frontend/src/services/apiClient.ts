import axios from 'axios';
import type { AxiosInstance, AxiosResponse, AxiosError } from 'axios';
import { message } from 'antd';
import { ensureBearerToken, debugToken } from '../utils/tokenUtils';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

interface ApiErrorResponse {
  message?: string;
  code?: string;
  details?: Record<string, unknown>;
}

class ApiClient {
  private instance: AxiosInstance;

  constructor() {
    this.instance = axios.create({
      baseURL: API_BASE_URL,
      timeout: 15000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // 请求拦截器
    this.instance.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token');
        console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`);
        console.log(`[Raw Token] ${token ? token.substring(0, 20) + '...' : 'null'}`);
        
        if (token && config.headers) {
          // 使用工具函数确保令牌格式正确
          const authToken = ensureBearerToken(token);
          if (authToken) {
            config.headers.Authorization = authToken;
            console.log(`[Authorization Header] ${authToken.substring(0, 30)}...`);
            console.log(`[Token starts with Bearer] ${authToken.startsWith('Bearer ')}`);
            
            // 额外的调试信息
            if (config.url?.includes('/auth/me')) {
              console.log('=== /auth/me 请求调试信息 ===');
              console.log('完整的Authorization头:', authToken);
              console.log('请求配置:', {
                url: config.url,
                method: config.method,
                headers: config.headers
              });
              debugToken();
            }
          } else {
            console.warn('无法生成有效的Authorization头');
          }
        } else {
          console.warn('未找到token或headers不存在');
        }
        return config;
      },
      (error) => {
        console.error('Request interceptor error:', error);
        return Promise.reject(error);
      }
    );

    // 响应拦截器 - 改进错误处理
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => response,
      (error: AxiosError<ApiErrorResponse>) => {
        this.handleError(error);
        return Promise.reject(error);
      }
    );
  }

  private handleError(error: AxiosError<ApiErrorResponse>) {
    if (error.response) {
      const { status, data } = error.response;
      
      switch (status) {
        case 401:
          this.handleUnauthorized();
          break;
        case 403:
          message.error('权限不足，无法访问该资源');
          break;
        case 404:
          message.error('请求的资源不存在');
          break;
        case 422:
          message.error(data?.message || '请求参数验证失败');
          break;
        case 500:
          message.error('服务器内部错误，请稍后重试');
          break;
        case 502:
        case 503:
          message.error('服务暂时不可用，请稍后重试');
          break;
        default:
          message.error(data?.message || `请求失败 (${status})`);
      }
    } else if (error.request) {
      message.error('网络连接失败，请检查网络设置');
    } else {
      message.error('请求配置错误');
    }
  }

  private handleUnauthorized() {
    localStorage.removeItem('token');
    // 避免在已经是登录页时重复跳转
    if (window.location.pathname !== '/login') {
      window.location.href = '/login';
      message.error('登录已过期，请重新登录');
    }
  }

  public get<T = unknown>(url: string, params?: Record<string, unknown>): Promise<AxiosResponse<T>> {
    return this.instance.get(url, { params });
  }

  public post<T = unknown>(url: string, data?: any): Promise<AxiosResponse<T>> {
    return this.instance.post(url, data);
  }

  public put<T = unknown>(url: string, data?: any): Promise<AxiosResponse<T>> {
    return this.instance.put(url, data);
  }

  public patch<T = unknown>(url: string, data?: any): Promise<AxiosResponse<T>> {
    return this.instance.patch(url, data);
  }

  public delete<T = unknown>(url: string): Promise<AxiosResponse<T>> {
    return this.instance.delete(url);
  }

  // 添加文件上传方法
  public uploadFile<T = unknown>(url: string, formData: FormData): Promise<AxiosResponse<T>> {
    return this.instance.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  // 添加下载文件方法
  public downloadFile(url: string, filename?: string): Promise<void> {
    return this.instance.get(url, {
      responseType: 'blob',
    }).then(response => {
      const blob = new Blob([response.data]);
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.download = filename || 'download';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(downloadUrl);
    });
  }

  // 调试方法：检查当前令牌格式
  public debugToken(): void {
    const token = localStorage.getItem('token');
    console.log('Current token:', token ? `${token.substring(0, 20)}...` : 'null');
    console.log('Token starts with Bearer:', token?.startsWith('Bearer '));
  }
}

const apiClient = new ApiClient();
export { apiClient };
export default apiClient;