import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token'); // 假设token存储在localStorage中
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // 统一错误处理
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，可能需要重定向到登录页
          console.error('Unauthorized, redirecting to login...');
          // window.location.href = '/login'; // 示例：重定向
          break;
        case 403:
          console.error('Forbidden: You do not have permission to access this resource.');
          break;
        case 404:
          console.error('Not Found: The requested resource could not be found.');
          break;
        case 500:
          console.error('Server Error: Something went wrong on the server.');
          break;
        default:
          console.error(`Error: ${error.response.status} - ${error.response.data.message || error.message}`);
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      console.error('No response received:', error.request);
    } else {
      // 在设置请求时触发了一些事情，导致错误
      console.error('Error setting up request:', error.message);
    }
    return Promise.reject(error);
  }
);

export default apiClient;