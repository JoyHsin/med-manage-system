// 分诊叫号相关类型定义

export interface PatientQueue {
  id: number;
  patientId: number;
  patientName: string;
  patientPhone: string;
  registrationId: number;
  registrationNumber: string;
  department: string;
  queueDate: string;
  queueNumber: number;
  status: QueueStatus;
  priority: number;
  calledAt?: string;
  arrivedAt?: string;
  completedAt?: string;
  callCount: number;
  notes?: string;
  calledBy?: number;
  confirmedBy?: number;
  createdAt: string;
  updatedAt: string;
}

export type QueueStatus = 'WAITING' | 'CALLED' | 'ARRIVED' | 'ABSENT' | 'COMPLETED';

export interface CallPatientRequest {
  patientQueueId: number;
  calledBy: number;
}

export interface QueueStatistics {
  totalWaiting: number;
  totalCalled: number;
  totalArrived: number;
  totalAbsent: number;
  totalCompleted: number;
  averageWaitTime: number;
}

export interface QueueFilters {
  status?: QueueStatus;
  queueDate?: string;
  department?: string;
  priority?: number;
}

// 状态显示配置
export const QUEUE_STATUS_CONFIG = {
  WAITING: {
    text: '等待中',
    color: 'default',
    badge: 'processing'
  },
  CALLED: {
    text: '已叫号',
    color: 'blue',
    badge: 'processing'
  },
  ARRIVED: {
    text: '已到达',
    color: 'green',
    badge: 'success'
  },
  ABSENT: {
    text: '未到',
    color: 'red',
    badge: 'error'
  },
  COMPLETED: {
    text: '已完成',
    color: 'gray',
    badge: 'default'
  }
} as const;

// 优先级配置
export const PRIORITY_CONFIG = {
  1: { text: '紧急', color: 'red' },
  2: { text: '高', color: 'orange' },
  3: { text: '普通', color: 'blue' },
  4: { text: '低', color: 'gray' },
  5: { text: '最低', color: 'gray' }
} as const;