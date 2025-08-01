package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.StockTransaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存交易数据访问层
 */
@Mapper
public interface StockTransactionMapper extends BaseMapper<StockTransaction> {

    /**
     * 根据交易编号查询
     */
    @Select("SELECT * FROM stock_transactions WHERE transaction_number = #{transactionNumber} AND deleted = 0")
    StockTransaction findByTransactionNumber(@Param("transactionNumber") String transactionNumber);

    /**
     * 根据药品ID查询交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE medicine_id = #{medicineId} AND deleted = 0 " +
            "ORDER BY transaction_date DESC")
    List<StockTransaction> findByMedicineId(@Param("medicineId") Long medicineId);

    /**
     * 根据交易类型查询
     */
    @Select("SELECT * FROM stock_transactions WHERE transaction_type = #{transactionType} AND deleted = 0 " +
            "ORDER BY transaction_date DESC")
    List<StockTransaction> findByTransactionType(@Param("transactionType") String transactionType);

    /**
     * 根据状态查询交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE status = #{status} AND deleted = 0 " +
            "ORDER BY transaction_date DESC")
    List<StockTransaction> findByStatus(@Param("status") String status);

    /**
     * 根据操作人员查询交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE operator_id = #{operatorId} AND deleted = 0 " +
            "ORDER BY transaction_date DESC")
    List<StockTransaction> findByOperatorId(@Param("operatorId") Long operatorId);

    /**
     * 根据时间范围查询交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE transaction_date BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0 ORDER BY transaction_date DESC")
    List<StockTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * 根据药品ID和时间范围查询交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE medicine_id = #{medicineId} " +
            "AND transaction_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 " +
            "ORDER BY transaction_date DESC")
    List<StockTransaction> findByMedicineIdAndDateRange(@Param("medicineId") Long medicineId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    /**
     * 根据批次号查询交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE batch_number = #{batchNumber} AND deleted = 0 " +
            "ORDER BY transaction_date DESC")
    List<StockTransaction> findByBatchNumber(@Param("batchNumber") String batchNumber);

    /**
     * 根据供应商ID查询入库记录
     */
    @Select("SELECT * FROM stock_transactions WHERE supplier_id = #{supplierId} " +
            "AND transaction_type = '入库' AND deleted = 0 ORDER BY transaction_date DESC")
    List<StockTransaction> findInboundBySupplier(@Param("supplierId") Long supplierId);

    /**
     * 查询待审核的交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE status = '待审核' AND deleted = 0 " +
            "ORDER BY transaction_date ASC")
    List<StockTransaction> findPendingTransactions();

    /**
     * 查询已确认的交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE status = '已确认' AND deleted = 0 " +
            "ORDER BY transaction_date DESC")
    List<StockTransaction> findConfirmedTransactions();

    /**
     * 根据相关单据号查询交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE related_document_number = #{documentNumber} " +
            "AND deleted = 0 ORDER BY transaction_date DESC")
    List<StockTransaction> findByRelatedDocumentNumber(@Param("documentNumber") String documentNumber);

    /**
     * 统计指定药品的入库总量
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM stock_transactions " +
            "WHERE medicine_id = #{medicineId} AND transaction_type = '入库' " +
            "AND status = '已确认' AND deleted = 0")
    Integer getTotalInboundQuantity(@Param("medicineId") Long medicineId);

    /**
     * 统计指定药品的出库总量
     */
    @Select("SELECT COALESCE(SUM(ABS(quantity)), 0) FROM stock_transactions " +
            "WHERE medicine_id = #{medicineId} AND transaction_type = '出库' " +
            "AND status = '已确认' AND deleted = 0")
    Integer getTotalOutboundQuantity(@Param("medicineId") Long medicineId);

    /**
     * 获取药品的最新交易记录
     */
    @Select("SELECT * FROM stock_transactions WHERE medicine_id = #{medicineId} AND deleted = 0 " +
            "ORDER BY transaction_date DESC LIMIT 1")
    StockTransaction getLatestTransactionByMedicine(@Param("medicineId") Long medicineId);

    /**
     * 统计指定时间范围内的交易数量
     */
    @Select("SELECT COUNT(*) FROM stock_transactions WHERE transaction_date BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0")
    Long countTransactionsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);

    /**
     * 统计各交易类型的数量
     */
    @Select("SELECT transaction_type, COUNT(*) as count FROM stock_transactions " +
            "WHERE deleted = 0 GROUP BY transaction_type")
    List<Object> countTransactionsByType();
}