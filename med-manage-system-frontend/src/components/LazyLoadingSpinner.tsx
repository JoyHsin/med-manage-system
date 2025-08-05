import React from 'react';
import { Spin, Progress } from 'antd';

interface LazyLoadingSpinnerProps {
  tip?: string;
  size?: 'small' | 'default' | 'large';
  priority?: 'high' | 'medium' | 'low';
  showProgress?: boolean;
}

const LazyLoadingSpinner: React.FC<LazyLoadingSpinnerProps> = ({ 
  tip = '正在加载页面...', 
  size = 'large',
  priority = 'medium',
  showProgress = false
}) => {
  const [progress, setProgress] = React.useState(0);

  React.useEffect(() => {
    if (!showProgress) return;

    const interval = setInterval(() => {
      setProgress(prev => {
        if (prev >= 90) return prev;
        return prev + Math.random() * 15;
      });
    }, 200);

    return () => clearInterval(interval);
  }, [showProgress]);

  // 根据优先级调整样式
  const getPriorityStyle = () => {
    switch (priority) {
      case 'high':
        return { color: '#1890ff', fontWeight: 'bold' };
      case 'low':
        return { color: '#999', fontSize: '12px' };
      default:
        return { color: '#666' };
    }
  };

  const getSpinnerSize = () => {
    if (priority === 'low') return 'default';
    return size;
  };

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: priority === 'low' ? '100px' : '200px',
      flexDirection: 'column',
      padding: '24px'
    }}>
      <Spin size={getSpinnerSize()} />
      <div style={{ 
        marginTop: '16px', 
        textAlign: 'center',
        ...getPriorityStyle()
      }}>
        {tip}
      </div>
      {showProgress && (
        <div style={{ marginTop: '12px', width: '200px' }}>
          <Progress 
            percent={Math.round(progress)} 
            size="small" 
            showInfo={false}
            strokeColor={priority === 'high' ? '#1890ff' : '#52c41a'}
          />
        </div>
      )}
      {priority === 'low' && (
        <div style={{ 
          marginTop: '8px', 
          fontSize: '11px', 
          color: '#ccc' 
        }}>
          低优先级加载
        </div>
      )}
    </div>
  );
};

export default LazyLoadingSpinner;