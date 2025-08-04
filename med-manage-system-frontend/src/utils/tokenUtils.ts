/**
 * 令牌处理工具函数
 */

/**
 * 确保令牌格式正确（添加Bearer前缀）
 */
export const ensureBearerToken = (token: string | null): string | null => {
  if (!token) return null;
  return token.startsWith('Bearer ') ? token : `Bearer ${token}`;
};

/**
 * 清理令牌（移除Bearer前缀）
 */
export const cleanToken = (token: string | null): string | null => {
  if (!token) return null;
  return token.startsWith('Bearer ') ? token.substring(7) : token;
};

/**
 * 验证令牌格式
 */
export const validateTokenFormat = (token: string | null): {
  isValid: boolean;
  hasBearer: boolean;
  length: number;
  message: string;
} => {
  if (!token) {
    return {
      isValid: false,
      hasBearer: false,
      length: 0,
      message: '令牌为空'
    };
  }

  const hasBearer = token.startsWith('Bearer ');
  const cleanTokenLength = hasBearer ? token.length - 7 : token.length;
  
  return {
    isValid: cleanTokenLength > 0,
    hasBearer,
    length: token.length,
    message: hasBearer 
      ? `令牌格式正确，包含Bearer前缀，总长度: ${token.length}` 
      : `令牌不包含Bearer前缀，长度: ${token.length}`
  };
};

/**
 * 调试令牌信息
 */
export const debugToken = (): void => {
  const token = localStorage.getItem('token');
  const validation = validateTokenFormat(token);
  
  console.group('🔍 Token Debug Info');
  console.log('原始令牌:', token ? `${token.substring(0, 30)}...` : 'null');
  console.log('验证结果:', validation);
  console.log('清理后的令牌:', cleanToken(token)?.substring(0, 30) + '...');
  console.log('Bearer格式令牌:', ensureBearerToken(token)?.substring(0, 30) + '...');
  console.groupEnd();
};