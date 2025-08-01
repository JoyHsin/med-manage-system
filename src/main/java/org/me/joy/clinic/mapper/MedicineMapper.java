package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Medicine;

import java.time.LocalDate;
import java.util.List;

/**
 * 药品数据访问层
 */
@Mapper
public interface MedicineMapper extends BaseMapper<Medicine> {

    /**
     * 根据药品编码查询药品
     */
    @Select("SELECT * FROM medicines WHERE medicine_code = #{medicineCode} AND deleted = 0")
    Medicine findByMedicineCode(@Param("medicineCode") String medicineCode);

    /**
     * 根据药品名称模糊查询
     */
    @Select("SELECT * FROM medicines WHERE (name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR generic_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR brand_name LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND deleted = 0 ORDER BY name")
    List<Medicine> findByNameContaining(@Param("keyword") String keyword);

    /**
     * 根据分类查询药品
     */
    @Select("SELECT * FROM medicines WHERE category = #{category} AND deleted = 0 ORDER BY name")
    List<Medicine> findByCategory(@Param("category") String category);

    /**
     * 根据生产厂家查询药品
     */
    @Select("SELECT * FROM medicines WHERE manufacturer LIKE CONCAT('%', #{manufacturer}, '%') " +
            "AND deleted = 0 ORDER BY name")
    List<Medicine> findByManufacturer(@Param("manufacturer") String manufacturer);

    /**
     * 查询启用的药品
     */
    @Select("SELECT * FROM medicines WHERE enabled = #{enabled} AND deleted = 0 ORDER BY name")
    List<Medicine> findByEnabled(@Param("enabled") Boolean enabled);

    /**
     * 查询处方药
     */
    @Select("SELECT * FROM medicines WHERE (category = '处方药' OR requires_prescription = true) " +
            "AND enabled = true AND deleted = 0 ORDER BY name")
    List<Medicine> findPrescriptionMedicines();

    /**
     * 查询非处方药
     */
    @Select("SELECT * FROM medicines WHERE category = '非处方药' AND requires_prescription = false " +
            "AND enabled = true AND deleted = 0 ORDER BY name")
    List<Medicine> findOverTheCounterMedicines();

    /**
     * 查询特殊管制药品
     */
    @Select("SELECT * FROM medicines WHERE is_controlled_substance = true " +
            "AND enabled = true AND deleted = 0 ORDER BY name")
    List<Medicine> findControlledSubstances();

    /**
     * 根据价格范围查询药品
     */
    @Select("SELECT * FROM medicines WHERE selling_price BETWEEN #{minPrice} AND #{maxPrice} " +
            "AND enabled = true AND deleted = 0 ORDER BY selling_price")
    List<Medicine> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * 查询需要补货的药品（基于最小库存水平）
     */
    @Select("SELECT m.* FROM medicines m " +
            "LEFT JOIN (SELECT medicine_id, SUM(current_stock) as total_stock " +
            "           FROM inventory_levels WHERE status = '正常' GROUP BY medicine_id) il " +
            "ON m.id = il.medicine_id " +
            "WHERE m.enabled = true AND m.deleted = 0 " +
            "AND (il.total_stock IS NULL OR il.total_stock <= m.min_stock_level) " +
            "ORDER BY m.name")
    List<Medicine> findMedicinesNeedingRestock();

    /**
     * 查询库存过多的药品（基于最大库存水平）
     */
    @Select("SELECT m.* FROM medicines m " +
            "INNER JOIN (SELECT medicine_id, SUM(current_stock) as total_stock " +
            "            FROM inventory_levels WHERE status = '正常' GROUP BY medicine_id) il " +
            "ON m.id = il.medicine_id " +
            "WHERE m.enabled = true AND m.deleted = 0 " +
            "AND il.total_stock > m.max_stock_level " +
            "ORDER BY m.name")
    List<Medicine> findOverstockedMedicines();

    /**
     * 统计药品总数
     */
    @Select("SELECT COUNT(*) FROM medicines WHERE enabled = true AND deleted = 0")
    Long countActiveMedicines();

    /**
     * 统计各分类药品数量
     */
    @Select("SELECT category, COUNT(*) as count FROM medicines " +
            "WHERE enabled = true AND deleted = 0 GROUP BY category")
    List<Object> countMedicinesByCategory();
}