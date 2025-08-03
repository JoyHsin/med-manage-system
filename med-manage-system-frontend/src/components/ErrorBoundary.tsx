import React, { Component, ErrorInfo, ReactNode } from 'react';
import { Result, Button, Typography } from 'antd';
import { ReloadOutlined, HomeOutlined } from '@ant-design/icons';

const { Paragraph, Text } = Typography;

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
  errorInfo?: ErrorInfo;
}

class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    this.setState({
      error,
      errorInfo,
    });

    // 这里可以添加错误日志上报
    console.error('ErrorBoundary caught an error:', error, errorInfo);
    
    // 可以将错误信息发送到监控服务
    // reportError(error, errorInfo);
  }

  handleReload = () => {
    window.location.reload();
  };

  handleGoHome = () => {
    window.location.href = '/dashboard';
  };

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div style={{ 
          padding: '50px',
          minHeight: '100vh',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}>
          <Result
            status="500"
            title="页面出现错误"
            subTitle="抱歉，页面发生了意外错误。请尝试刷新页面或返回首页。"
            extra={[
              <Button type="primary" icon={<ReloadOutlined />} onClick={this.handleReload} key="reload">
                刷新页面
              </Button>,
              <Button icon={<HomeOutlined />} onClick={this.handleGoHome} key="home">
                返回首页
              </Button>,
            ]}
          >
            {process.env.NODE_ENV === 'development' && this.state.error && (
              <div style={{ textAlign: 'left', marginTop: 16 }}>
                <Typography>
                  <Paragraph>
                    <Text strong>错误详情 (仅开发环境显示):</Text>
                  </Paragraph>
                  <Paragraph>
                    <Text code>{this.state.error.toString()}</Text>
                  </Paragraph>
                  {this.state.errorInfo && (
                    <Paragraph>
                      <Text strong>组件堆栈:</Text>
                      <pre style={{ 
                        whiteSpace: 'pre-wrap',
                        fontSize: '12px',
                        background: '#f5f5f5',
                        padding: '8px',
                        borderRadius: '4px',
                        maxHeight: '200px',
                        overflow: 'auto'
                      }}>
                        {this.state.errorInfo.componentStack}
                      </pre>
                    </Paragraph>
                  )}
                </Typography>
              </div>
            )}
          </Result>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;