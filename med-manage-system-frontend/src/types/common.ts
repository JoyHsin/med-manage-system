// 通用类型定义文件

// API 错误响应类型
export interface ApiError {
  response?: {
    data?: {
      message?: string;
      code?: string;
    };
    status?: number;
  };
  message?: string;
}

// 分页参数
export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
  order?: 'asc' | 'desc';
}

// 分页响应
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// 下拉选项
export interface SelectOption {
  label: string;
  value: string | number;
  disabled?: boolean;
}

// 表格列配置
export interface TableColumn<T = unknown> {
  key: string;
  title: string;
  dataIndex?: keyof T;
  width?: number;
  fixed?: 'left' | 'right';
  render?: (value: unknown, record: T, index: number) => React.ReactNode;
  sorter?: boolean;
  filters?: { text: string; value: string }[];
}

// 表单字段规则
export interface FormRule {
  required?: boolean;
  message?: string;
  pattern?: RegExp;
  min?: number;
  max?: number;
  validator?: (rule: unknown, value: unknown) => Promise<void>;
}

// 通用 ID 类型
export type ID = string | number;

// 通用状态枚举
export const CommonStatus = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  PENDING: 'PENDING',
  DELETED: 'DELETED',
} as const;

// 性别枚举
export const Gender = {
  MALE: '男',
  FEMALE: '女',
  UNKNOWN: '未知',
} as const;

// 通用响应类型
export interface BaseResponse {
  success: boolean;
  message?: string;
  timestamp: number;
}

export interface DataResponse<T = unknown> extends BaseResponse {
  data: T;
}

export interface ListResponse<T = unknown> extends BaseResponse {
  data: T[];
  total?: number;
}

export interface PaginatedListResponse<T = unknown> extends BaseResponse {
  data: PaginatedResponse<T>;
}