package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.InsuranceProvider;

import java.util.List;

/**
 * 保险提供商数据访问接口
 */
@Mapper
public interface InsuranceProviderMapper extends BaseMapper<InsuranceProvider> {

    /**
     * 根据名称查询保险提供商
     */
    @Select("SELECT * FROM insurance_providers WHERE name = #{name} AND deleted = 0")
    InsuranceProvider findByName(@Param("name") String name);

    /**
     * 根据代码查询保险提供商
     */
    @Select("SELECT * FROM insurance_providers WHERE code = #{code} AND deleted = 0")
    InsuranceProvider findByCode(@Param("code") String code);

    /**
     * 查询启用的保险提供商
     */
    @Select("SELECT * FROM insurance_providers WHERE enabled = 1 AND deleted = 0 ORDER BY name ASC")
    List<InsuranceProvider> findEnabled();

    /**
     * 查询禁用的保险提供商
     */
    @Select("SELECT * FROM insurance_providers WHERE enabled = 0 AND deleted = 0 ORDER BY name ASC")
    List<InsuranceProvider> findDisabled();

    /**
     * 根据类型查询保险提供商
     */
    @Select("SELECT * FROM insurance_providers WHERE type = #{type} AND deleted = 0 ORDER BY name ASC")
    List<InsuranceProvider> findByType(@Param("type") String type);

    /**
     * 检查名称是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM insurance_providers WHERE name = #{name} AND deleted = 0")
    boolean existsByName(@Param("name") String name);

    /**
     * 检查代码是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM insurance_providers WHERE code = #{code} AND deleted = 0")
    boolean existsByCode(@Param("code") String code);

    /**
     * 统计启用的保险提供商数量
     */
    @Select("SELECT COUNT(*) FROM insurance_providers WHERE enabled = 1 AND deleted = 0")
    int countEnabled();

    /**
     * 统计禁用的保险提供商数量
     */
    @Select("SELECT COUNT(*) FROM insurance_providers WHERE enabled = 0 AND deleted = 0")
    int countDisabled();
}