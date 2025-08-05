/**
 * 报表导出工具函数
 */

export interface ExportOptions {
  format: 'excel' | 'pdf';
  includeCharts: boolean;
  includeRawData: boolean;
  pageOrientation?: 'portrait' | 'landscape';
  pageSize?: 'A4' | 'A3' | 'Letter';
  compression?: 'none' | 'low' | 'medium' | 'high';
  password?: string;
  watermark?: string;
}

/**
 * 下载文件
 */
export const downloadFile = (blob: Blob, filename: string): void => {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};

/**
 * 获取文件MIME类型
 */
export const getMimeType = (format: string): string => {
  const mimeTypes: Record<string, string> = {
    excel: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    pdf: 'application/pdf',
    csv: 'text/csv',
    json: 'application/json',
    xml: 'application/xml'
  };
  return mimeTypes[format] || 'application/octet-stream';
};

/**
 * 格式化文件大小
 */
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B';
  
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

/**
 * 生成文件名
 */
export const generateFileName = (
  baseName: string, 
  format: string, 
  timestamp?: string
): string => {
  const extensions: Record<string, string> = {
    excel: 'xlsx',
    pdf: 'pdf',
    csv: 'csv',
    json: 'json',
    xml: 'xml'
  };
  
  const ext = extensions[format] || 'txt';
  const time = timestamp || new Date().toISOString().slice(0, 19).replace(/[:\-T]/g, '');
  const safeName = baseName.replace(/[^a-zA-Z0-9\u4e00-\u9fa5]/g, '_');
  
  return `${safeName}_${time}.${ext}`;
};

/**
 * 验证导出选项
 */
export const validateExportOptions = (options: ExportOptions): {
  valid: boolean;
  errors: string[];
} => {
  const errors: string[] = [];

  // 验证格式
  if (!['excel', 'pdf'].includes(options.format)) {
    errors.push('不支持的导出格式');
  }

  // 验证页面方向
  if (options.pageOrientation && !['portrait', 'landscape'].includes(options.pageOrientation)) {
    errors.push('无效的页面方向');
  }

  // 验证页面大小
  if (options.pageSize && !['A4', 'A3', 'Letter'].includes(options.pageSize)) {
    errors.push('无效的页面大小');
  }

  // 验证压缩级别
  if (options.compression && !['none', 'low', 'medium', 'high'].includes(options.compression)) {
    errors.push('无效的压缩级别');
  }

  // 验证密码长度
  if (options.password && options.password.length < 4) {
    errors.push('密码长度至少4位');
  }

  return {
    valid: errors.length === 0,
    errors
  };
};

/**
 * 创建打印样式
 */
export const createPrintStyles = (): string => {
  return `
    <style>
      @media print {
        body {
          margin: 0;
          padding: 20px;
          font-family: Arial, sans-serif;
          font-size: 12px;
          line-height: 1.4;
        }
        
        .no-print {
          display: none !important;
        }
        
        .print-header {
          text-align: center;
          margin-bottom: 20px;
          border-bottom: 2px solid #333;
          padding-bottom: 10px;
        }
        
        .print-header h1 {
          margin: 0;
          font-size: 18px;
          color: #333;
        }
        
        .print-header p {
          margin: 5px 0 0 0;
          color: #666;
          font-size: 10px;
        }
        
        .print-content {
          margin: 20px 0;
        }
        
        .print-table {
          width: 100%;
          border-collapse: collapse;
          margin: 10px 0;
        }
        
        .print-table th,
        .print-table td {
          border: 1px solid #ddd;
          padding: 8px;
          text-align: left;
        }
        
        .print-table th {
          background-color: #f5f5f5;
          font-weight: bold;
        }
        
        .print-footer {
          position: fixed;
          bottom: 20px;
          left: 20px;
          right: 20px;
          text-align: center;
          font-size: 10px;
          color: #666;
          border-top: 1px solid #ddd;
          padding-top: 10px;
        }
        
        .page-break {
          page-break-before: always;
        }
        
        .chart-container {
          text-align: center;
          margin: 20px 0;
        }
        
        .chart-container img {
          max-width: 100%;
          height: auto;
        }
      }
      
      @page {
        margin: 2cm;
        size: A4;
      }
    </style>
  `;
};

/**
 * 生成打印HTML
 */
export const generatePrintHTML = (
  title: string,
  content: string,
  options?: {
    includeHeader?: boolean;
    includeFooter?: boolean;
    timestamp?: string;
  }
): string => {
  const { includeHeader = true, includeFooter = true, timestamp } = options || {};
  const currentTime = timestamp || new Date().toLocaleString('zh-CN');
  
  return `
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="utf-8">
        <title>${title}</title>
        ${createPrintStyles()}
      </head>
      <body>
        ${includeHeader ? `
          <div class="print-header">
            <h1>${title}</h1>
            <p>生成时间: ${currentTime}</p>
          </div>
        ` : ''}
        
        <div class="print-content">
          ${content}
        </div>
        
        ${includeFooter ? `
          <div class="print-footer">
            <p>© 社区诊所管理系统 - 报表打印于 ${currentTime}</p>
          </div>
        ` : ''}
      </body>
    </html>
  `;
};

/**
 * 执行打印
 */
export const executePrint = (htmlContent: string): void => {
  const printWindow = window.open('', '_blank');
  if (printWindow) {
    printWindow.document.write(htmlContent);
    printWindow.document.close();
    
    // 等待内容加载完成后打印
    printWindow.onload = () => {
      printWindow.print();
      printWindow.close();
    };
  }
};

/**
 * 复制到剪贴板
 */
export const copyToClipboard = async (text: string): Promise<boolean> => {
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text);
      return true;
    } else {
      // 降级方案
      const textArea = document.createElement('textarea');
      textArea.value = text;
      textArea.style.position = 'fixed';
      textArea.style.left = '-999999px';
      textArea.style.top = '-999999px';
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      const result = document.execCommand('copy');
      document.body.removeChild(textArea);
      return result;
    }
  } catch (error) {
    console.error('复制失败:', error);
    return false;
  }
};

/**
 * 生成分享二维码
 */
export const generateQRCode = (url: string, size: number = 200): string => {
  // 这里应该使用实际的二维码生成库，比如 qrcode.js
  // 现在返回一个模拟的二维码数据URL
  return `data:image/svg+xml;base64,${btoa(`
    <svg width="${size}" height="${size}" xmlns="http://www.w3.org/2000/svg">
      <rect width="100%" height="100%" fill="white"/>
      <text x="50%" y="50%" text-anchor="middle" dy=".3em" font-family="Arial" font-size="12">
        QR Code
      </text>
      <text x="50%" y="60%" text-anchor="middle" dy=".3em" font-family="Arial" font-size="8">
        ${url.substring(0, 30)}...
      </text>
    </svg>
  `)}`;
};

/**
 * 验证邮箱地址
 */
export const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * 验证邮箱列表
 */
export const validateEmailList = (emails: string[]): {
  valid: string[];
  invalid: string[];
} => {
  const valid: string[] = [];
  const invalid: string[] = [];
  
  emails.forEach(email => {
    const trimmedEmail = email.trim();
    if (validateEmail(trimmedEmail)) {
      valid.push(trimmedEmail);
    } else {
      invalid.push(trimmedEmail);
    }
  });
  
  return { valid, invalid };
};

/**
 * 格式化分享链接
 */
export const formatShareUrl = (baseUrl: string, reportId: string, options?: {
  token?: string;
  expires?: number;
  password?: boolean;
}): string => {
  const url = new URL(`${baseUrl}/shared-reports/${reportId}`);
  
  if (options?.token) {
    url.searchParams.set('token', options.token);
  }
  
  if (options?.expires) {
    url.searchParams.set('expires', options.expires.toString());
  }
  
  if (options?.password) {
    url.searchParams.set('protected', '1');
  }
  
  return url.toString();
};

export default {
  downloadFile,
  getMimeType,
  formatFileSize,
  generateFileName,
  validateExportOptions,
  createPrintStyles,
  generatePrintHTML,
  executePrint,
  copyToClipboard,
  generateQRCode,
  validateEmail,
  validateEmailList,
  formatShareUrl
};