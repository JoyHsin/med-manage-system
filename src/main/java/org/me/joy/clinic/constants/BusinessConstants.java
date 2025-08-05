package org.me.joy.clinic.constants;

/**
 * 业务常量类
 * 
 * 用于定义系统中使用的业务相关常量，避免魔法数字的使用
 */
public final class BusinessConstants {
    
    private BusinessConstants() {
        // 私有构造函数，防止实例化
    }
    
    /**
     * 时间相关常量
     */
    public static final class TimeConstants {
        /** 患者留存报告统计时间范围（天数） */
        public static final int RETENTION_REPORT_DAYS = 30;
        
        /** 保险即将过期提醒时间范围（天数） */
        public static final int INSURANCE_EXPIRY_WARNING_DAYS = 30;
        
        /** 药品过期提醒时间范围（天数） */
        public static final int MEDICINE_EXPIRY_WARNING_DAYS = 30;
        
        /** JWT令牌默认过期时间（分钟） */
        public static final int JWT_EXPIRATION_MINUTES = 30;
    }
    
    /**
     * 文件大小限制常量
     */
    public static final class FileSizeConstants {
        /** 病历编号最大长度 */
        public static final int MEDICAL_RECORD_NUMBER_MAX_LENGTH = 50;
        
        /** 主诉最大长度 */
        public static final int CHIEF_COMPLAINT_MAX_LENGTH = 1000;
        
        /** 现病史最大长度 */
        public static final int PRESENT_ILLNESS_MAX_LENGTH = 2000;
        
        /** 既往史最大长度 */
        public static final int PAST_HISTORY_MAX_LENGTH = 2000;
        
        /** 个人史最大长度 */
        public static final int PERSONAL_HISTORY_MAX_LENGTH = 1000;
        
        /** 家族史最大长度 */
        public static final int FAMILY_HISTORY_MAX_LENGTH = 1000;
        
        /** 体格检查最大长度 */
        public static final int PHYSICAL_EXAMINATION_MAX_LENGTH = 2000;
        
        /** 辅助检查最大长度 */
        public static final int AUXILIARY_EXAMINATION_MAX_LENGTH = 2000;
        
        /** 诊断最大长度 */
        public static final int DIAGNOSIS_MAX_LENGTH = 1000;
        
        /** 治疗方案最大长度 */
        public static final int TREATMENT_PLAN_MAX_LENGTH = 2000;
        
        /** 医嘱最大长度 */
        public static final int MEDICAL_ADVICE_MAX_LENGTH = 2000;
        
        /** 病情评估最大长度 */
        public static final int CONDITION_ASSESSMENT_MAX_LENGTH = 1000;
        
        /** 预后最大长度 */
        public static final int PROGNOSIS_MAX_LENGTH = 500;
        
        /** 随访建议最大长度 */
        public static final int FOLLOW_UP_ADVICE_MAX_LENGTH = 1000;
        
        /** 审核意见最大长度 */
        public static final int REVIEW_COMMENT_MAX_LENGTH = 500;
        
        /** 科室名称最大长度 */
        public static final int DEPARTMENT_NAME_MAX_LENGTH = 100;
    }
    
    /**
     * 网络相关常量
     */
    public static final class NetworkConstants {
        /** 开发环境前端端口1 */
        public static final int DEV_FRONTEND_PORT_1 = 5173;
        
        /** 开发环境前端地址1 */
        public static final String DEV_FRONTEND_URL_1 = "http://localhost:5173";
        
        /** 开发环境前端地址2 */
        public static final String DEV_FRONTEND_URL_2 = "http://127.0.0.1:5173";
    }
    
    /**
     * 数据库相关常量
     */
    public static final class DatabaseConstants {
        /** 软删除标记 - 未删除 */
        public static final int NOT_DELETED = 0;
        
        /** 软删除标记 - 已删除 */
        public static final int DELETED = 1;
    }
}