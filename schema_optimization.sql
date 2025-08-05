-- 患者表地区分布查询性能优化建议
-- 文件: schema_optimization.sql
-- 日期: 2025-01-27
-- 目的: 解决 PatientMapper.xml 中地区分布查询的性能问题

-- 步骤1: 在patients表中添加city和province字段
ALTER TABLE patients 
ADD COLUMN city VARCHAR(50) NULL COMMENT '城市',
ADD COLUMN province VARCHAR(50) NULL COMMENT '省份';

-- 步骤2: 创建索引以提升查询性能
CREATE INDEX idx_patients_city ON patients(city);
CREATE INDEX idx_patients_province ON patients(province);
CREATE INDEX idx_patients_city_province ON patients(city, province);

-- 步骤3: 从现有address字段提取城市和省份信息（数据迁移脚本示例）
-- 注意: 这个脚本需要根据实际地址格式进行调整
UPDATE patients 
SET 
    city = CASE 
        WHEN address LIKE '%北京市%' THEN '北京市'
        WHEN address LIKE '%上海市%' THEN '上海市'
        WHEN address LIKE '%广州市%' THEN '广州市'
        WHEN address LIKE '%深圳市%' THEN '深圳市'
        WHEN address LIKE '%杭州市%' THEN '杭州市'
        WHEN address LIKE '%南京市%' THEN '南京市'
        WHEN address LIKE '%武汉市%' THEN '武汉市'
        WHEN address LIKE '%成都市%' THEN '成都市'
        WHEN address LIKE '%西安市%' THEN '西安市'
        WHEN address LIKE '%重庆市%' THEN '重庆市'
        -- 可以继续添加更多城市匹配规则
        ELSE NULL
    END,
    province = CASE 
        WHEN address LIKE '%北京%' THEN '北京市'
        WHEN address LIKE '%上海%' THEN '上海市'
        WHEN address LIKE '%广东%' OR address LIKE '%广州%' OR address LIKE '%深圳%' THEN '广东省'
        WHEN address LIKE '%浙江%' OR address LIKE '%杭州%' THEN '浙江省'
        WHEN address LIKE '%江苏%' OR address LIKE '%南京%' THEN '江苏省'
        WHEN address LIKE '%湖北%' OR address LIKE '%武汉%' THEN '湖北省'
        WHEN address LIKE '%四川%' OR address LIKE '%成都%' THEN '四川省'
        WHEN address LIKE '%陕西%' OR address LIKE '%西安%' THEN '陕西省'
        WHEN address LIKE '%重庆%' THEN '重庆市'
        -- 可以继续添加更多省份匹配规则
        ELSE NULL
    END
WHERE address IS NOT NULL AND address != '';

-- 步骤4: 优化后的PatientMapper.xml查询语句（替换原有的getLocationDistribution方法）
/*
<select id="getLocationDistribution" resultType="java.util.Map">
    SELECT 
        COALESCE(city, province, '其他') as location,
        COUNT(*) as count
    FROM patients 
    WHERE deleted = 0 AND (city IS NOT NULL OR province IS NOT NULL)
    GROUP BY location
    ORDER BY count DESC
    LIMIT 10
</select>
*/

-- 步骤5: 可选 - 如果不再需要复杂的字符串函数查询，可以移除原有的address字段索引（如果存在）
-- DROP INDEX idx_patients_address ON patients;

-- 性能提升预期:
-- 1. 查询速度提升: 从O(n)字符串函数计算降低到O(log n)索引查询
-- 2. 数据准确性提升: 标准化的城市和省份字段避免字符串解析错误
-- 3. 维护性提升: 新增患者时可以直接填入标准化的城市和省份信息

-- 使用建议:
-- 1. 在测试环境先验证数据迁移脚本的准确性
-- 2. 生产环境执行前备份数据
-- 3. 考虑在业务低峰期执行迁移
-- 4. 更新应用程序代码以在创建/更新患者时填入city和province字段