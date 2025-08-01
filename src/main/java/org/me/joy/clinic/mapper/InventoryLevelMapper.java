package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.InventoryLevel;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存水平数据访问层
 */
@Mapper
public interface InventoryLevelMapper extends BaseMapper<InventoryLevel> {

    /**
     * 根据药品ID查询库存水平
     */
    @Select("SELECT * FROM inventory_levels WHERE medicine_id = #{medicineId} AND deleted = 0 " +
            "ORDER BY expiry_date ASC")
    List<InventoryLevel> findByMedicineId(@Param("medicineId") Long medicineId);

    /**
     * 根据药品ID和批次号查询库存水平
     */
    @Select("SELECT * FROM inventory_levels WHERE medicine_id = #{medicineId} " +
            "AND batch_number = #{batchNumber} AND deleted = 0")
    InventoryLevel findByMedicineIdAndBatchNumber(@Param("medicineId") Long medicineId, 
                                                @Param("batchNumber") String batchNumber);

    /**
     * 根据批次号查询库存水平
     */
    @Select("SELECT * FROM inventory_levels WHERE batch_number = #{batchNumber} AND deleted = 0")
    List<InventoryLevel> findByBatchNumber(@Param("batchNumber") String batchNumber);

    /**
     * 根据状态查询库存水平
     */
    @Select("SELECT * FROM inventory_levels WHERE status = #{status} AND deleted = 0 " +
            "ORDER BY expiry_date ASC")
    List<InventoryLevel> findByStatus(@Param("status") String status);

    /**
     * 查询正常状态的库存
     */
    @Select("SELECT * FROM inventory_levels WHERE status = '正常' AND current_stock > 0 " +
            "AND deleted = 0 ORDER BY expiry_date ASC")
    List<InventoryLevel> findNormalStock();

    /**
     * 查询可用库存（正常状态且有可用数量）
     */
    @Select("SELECT * FROM inventory_levels WHERE status = '正常' AND available_stock > 0 " +
            "AND deleted = 0 ORDER BY expiry_date ASC")
    List<InventoryLevel> findAvailableStock();

    /**
     * 根据药品ID查询可用库存
     */
    @Select("SELECT * FROM inventory_levels WHERE medicine_id = #{medicineId} " +
            "AND status = '正常' AND available_stock > 0 AND deleted = 0 " +
            "ORDER BY expiry_date ASC")
    List<InventoryLevel> findAvailableStockByMedicine(@Param("medicineId") Long medicineId);

    /**
     * 查询即将过期的库存（指定天数内）
     */
    @Select("SELECT * FROM inventory_levels WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL #{days} DAY) " +
            "AND current_stock > 0 AND status = '正常' AND deleted = 0 " +
            "ORDER BY expiry_date ASC")
    List<InventoryLevel> findExpiringSoon(@Param("days") Integer days);

    /**
     * 查询已过期的库存
     */
    @Select("SELECT * FROM inventory_levels WHERE expiry_date < CURDATE() " +
            "AND current_stock > 0 AND deleted = 0 ORDER BY expiry_date ASC")
    List<InventoryLevel> findExpiredStock();

    /**
     * 根据供应商ID查询库存
     */
    @Select("SELECT * FROM inventory_levels WHERE supplier_id = #{supplierId} AND deleted = 0 " +
            "ORDER BY expiry_date ASC")
    List<InventoryLevel> findBySupplier(@Param("supplierId") Long supplierId);

    /**
     * 根据存储位置查询库存
     */
    @Select("SELECT * FROM inventory_levels WHERE storage_location = #{location} AND deleted = 0 " +
            "ORDER BY expiry_date ASC")
    List<InventoryLevel> findByStorageLocation(@Param("location") String location);

    /**
     * 查询低库存的药品（基于最小库存水平）
     */
    @Select("SELECT il.* FROM inventory_levels il " +
            "INNER JOIN medicines m ON il.medicine_id = m.id " +
            "WHERE il.status = '正常' AND il.current_stock <= m.min_stock_level " +
            "AND il.deleted = 0 AND m.deleted = 0 " +
            "ORDER BY il.current_stock ASC")
    List<InventoryLevel> findLowStock();

    /**
     * 查询零库存的药品
     */
    @Select("SELECT * FROM inventory_levels WHERE current_stock = 0 AND deleted = 0")
    List<InventoryLevel> findZeroStock();

    /**
     * 统计指定药品的总库存
     */
    @Select("SELECT COALESCE(SUM(current_stock), 0) FROM inventory_levels " +
            "WHERE medicine_id = #{medicineId} AND status = '正常' AND deleted = 0")
    Integer getTotalStockByMedicine(@Param("medicineId") Long medicineId);

    /**
     * 统计指定药品的可用库存
     */
    @Select("SELECT COALESCE(SUM(available_stock), 0) FROM inventory_levels " +
            "WHERE medicine_id = #{medicineId} AND status = '正常' AND deleted = 0")
    Integer getAvailableStockByMedicine(@Param("medicineId") Long medicineId);

    /**
     * 统计指定药品的预留库存
     */
    @Select("SELECT COALESCE(SUM(reserved_stock), 0) FROM inventory_levels " +
            "WHERE medicine_id = #{medicineId} AND status = '正常' AND deleted = 0")
    Integer getReservedStockByMedicine(@Param("medicineId") Long medicineId);

    /**
     * 统计指定药品的锁定库存
     */
    @Select("SELECT COALESCE(SUM(locked_stock), 0) FROM inventory_levels " +
            "WHERE medicine_id = #{medicineId} AND status = '正常' AND deleted = 0")
    Integer getLockedStockByMedicine(@Param("medicineId") Long medicineId);

    /**
     * 获取指定药品最早过期的批次
     */
    @Select("SELECT * FROM inventory_levels WHERE medicine_id = #{medicineId} " +
            "AND status = '正常' AND available_stock > 0 AND deleted = 0 " +
            "ORDER BY expiry_date ASC LIMIT 1")
    InventoryLevel getEarliestExpiringBatch(@Param("medicineId") Long medicineId);

    /**
     * 统计库存总价值
     */
    @Select("SELECT COALESCE(SUM(inventory_cost), 0) FROM inventory_levels " +
            "WHERE status = '正常' AND deleted = 0")
    Double getTotalInventoryValue();

    /**
     * 统计指定药品的库存价值
     */
    @Select("SELECT COALESCE(SUM(inventory_cost), 0) FROM inventory_levels " +
            "WHERE medicine_id = #{medicineId} AND status = '正常' AND deleted = 0")
    Double getInventoryValueByMedicine(@Param("medicineId") Long medicineId);

    /**
     * 统计各状态库存数量
     */
    @Select("SELECT status, COUNT(*) as count FROM inventory_levels " +
            "WHERE deleted = 0 GROUP BY status")
    List<Object> countInventoryByStatus();
}