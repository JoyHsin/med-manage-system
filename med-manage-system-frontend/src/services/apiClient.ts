import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';
import { message } from 'antd';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

class ApiClient {
  private instance: AxiosInstance;

  constructor() {
    this.instance = axios.create({
      baseURL: API_BASE_URL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    this.instance.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token');
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    this.instance.interceptors.response.use(
      (response: AxiosResponse) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('token');
          window.location.href = '/login';
          message.error('登录已过期，请重新登录');
        } else if (error.response?.data?.message) {
          message.error(error.response.data.message);
        } else {
          message.error('网络错误，请检查网络连接');
        }
        return Promise.reject(error);
      }
    );
  }

  public get<T = any>(url: string, params?: any): Promise<AxiosResponse<T>> {
    return this.instance.get(url, { params });
  }

  public post<T = any>(url: string, data?: any): Promise<AxiosResponse<T>> {
    return this.instance.post(url, data);
  }

  public put<T = any>(url: string, data?: any): Promise<AxiosResponse<T>> {
    return this.instance.put(url, data);
  }

  public delete<T = any>(url: string): Promise<AxiosResponse<T>> {
    return this.instance.delete(url);
  }
}

const apiClient = new ApiClient();
export { apiClient };
export default apiClient;