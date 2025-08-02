package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.AddBillItemRequest;
import org.me.joy.clinic.dto.CreateBillRequest;
import org.me.joy.clinic.entity.Bill;
import org.me.joy.clinic.entity.BillItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 账单管理服务接口
 */
public interface BillingService {
    
    /**
     * 创建账单
     */
    Bill createBill(CreateBillRequest request);
    
    /**
     * 根据挂号ID计算账单
     */
    Bill calculateBill(Long registrationId);
    
    /**
     * 添加账单项目
     */
    BillItem addBillItem(Long billId, AddBillItemRequest request);
    
    /**
     * 根据ID获取账单
     */
    Bill getBillById(Long billId);
    
    /**
     * 根据账单编号获取账单
     */
    Bill getBillByNumber(String billNumber);
    
    /**
     * 获取患者的所有账单
     */
    List<Bill> getPatientBills(Long patientId);
    
    /**
     * 获取账单的所有项目
     */
    List<BillItem> getBillItems(Long billId);
    
    /**
     * 更新账单总金额
     */
    void updateBillTotalAmount(Long billId);
    
    /**
     * 更新账单支付状态
     */
    void updatePaymentStatus(Long billId, BigDecimal paidAmount);
    
    /**
     * 取消账单
     */
    void cancelBill(Long billId, String reason);
    
    /**
     * 根据状态获取账单列表
     */
    List<Bill> getBillsByStatus(String status);
    
    /**
     * 获取指定日期范围内的账单
     */
    List<Bill> getBillsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 计算指定日期的总收入
     */
    BigDecimal getTotalRevenueByDate(LocalDate date);
    
    /**
     * 删除账单项目
     */
    void removeBillItem(Long billItemId);
    
    /**
     * 更新账单项目
     */
    BillItem updateBillItem(Long billItemId, AddBillItemRequest request);
}