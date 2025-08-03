package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.AddBillItemRequest;
import org.me.joy.clinic.dto.CreateBillRequest;
import org.me.joy.clinic.entity.Bill;
import org.me.joy.clinic.entity.BillItem;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账单管理控制器
 */
@RestController
@RequestMapping("/billing")
public class BillingController {
    
    @Autowired
    private BillingService billingService;
    
    /**
     * 创建账单
     */
    @PostMapping("/bills")
    @RequiresPermission("BILLING_CREATE")
    public ResponseEntity<Bill> createBill(@Valid @RequestBody CreateBillRequest request) {
        Bill bill = billingService.createBill(request);
        return ResponseEntity.ok(bill);
    }
    
    /**
     * 根据挂号ID计算账单
     */
    @PostMapping("/bills/calculate/{registrationId}")
    @RequiresPermission("BILLING_CREATE")
    public ResponseEntity<Bill> calculateBill(@PathVariable Long registrationId) {
        Bill bill = billingService.calculateBill(registrationId);
        return ResponseEntity.ok(bill);
    }
    
    /**
     * 根据ID获取账单
     */
    @GetMapping("/bills/{billId}")
    @RequiresPermission("BILLING_VIEW")
    public ResponseEntity<Bill> getBillById(@PathVariable Long billId) {
        Bill bill = billingService.getBillById(billId);
        return ResponseEntity.ok(bill);
    }
    
    /**
     * 根据账单编号获取账单
     */
    @GetMapping("/bills/number/{billNumber}")
    @RequiresPermission("BILLING_VIEW")
    public ResponseEntity<Bill> getBillByNumber(@PathVariable String billNumber) {
        Bill bill = billingService.getBillByNumber(billNumber);
        return ResponseEntity.ok(bill);
    }
    
    /**
     * 获取患者的所有账单
     */
    @GetMapping("/bills/patient/{patientId}")
    @RequiresPermission("BILLING_VIEW")
    public ResponseEntity<List<Bill>> getPatientBills(@PathVariable Long patientId) {
        List<Bill> bills = billingService.getPatientBills(patientId);
        return ResponseEntity.ok(bills);
    }
    
    /**
     * 获取账单的所有项目
     */
    @GetMapping("/bills/{billId}/items")
    @RequiresPermission("BILLING_VIEW")
    public ResponseEntity<List<BillItem>> getBillItems(@PathVariable Long billId) {
        List<BillItem> items = billingService.getBillItems(billId);
        return ResponseEntity.ok(items);
    }
    
    /**
     * 添加账单项目
     */
    @PostMapping("/bills/{billId}/items")
    @RequiresPermission("BILLING_UPDATE")
    public ResponseEntity<BillItem> addBillItem(@PathVariable Long billId, 
                                               @Valid @RequestBody AddBillItemRequest request) {
        BillItem billItem = billingService.addBillItem(billId, request);
        return ResponseEntity.ok(billItem);
    }
    
    /**
     * 更新账单项目
     */
    @PutMapping("/bills/items/{billItemId}")
    @RequiresPermission("BILLING_UPDATE")
    public ResponseEntity<BillItem> updateBillItem(@PathVariable Long billItemId,
                                                  @Valid @RequestBody AddBillItemRequest request) {
        BillItem billItem = billingService.updateBillItem(billItemId, request);
        return ResponseEntity.ok(billItem);
    }
    
    /**
     * 删除账单项目
     */
    @DeleteMapping("/bills/items/{billItemId}")
    @RequiresPermission("BILLING_UPDATE")
    public ResponseEntity<Void> removeBillItem(@PathVariable Long billItemId) {
        billingService.removeBillItem(billItemId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 更新支付状态
     */
    @PutMapping("/bills/{billId}/payment")
    @RequiresPermission("BILLING_UPDATE")
    public ResponseEntity<Void> updatePaymentStatus(@PathVariable Long billId,
                                                   @RequestParam BigDecimal paidAmount) {
        billingService.updatePaymentStatus(billId, paidAmount);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 取消账单
     */
    @PutMapping("/bills/{billId}/cancel")
    @RequiresPermission("BILLING_UPDATE")
    public ResponseEntity<Void> cancelBill(@PathVariable Long billId,
                                          @RequestParam String reason) {
        billingService.cancelBill(billId, reason);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 根据状态获取账单列表
     */
    @GetMapping("/bills/status/{status}")
    @RequiresPermission("BILLING_VIEW")
    public ResponseEntity<List<Bill>> getBillsByStatus(@PathVariable String status) {
        List<Bill> bills = billingService.getBillsByStatus(status);
        return ResponseEntity.ok(bills);
    }
    
    /**
     * 获取指定日期范围内的账单
     */
    @GetMapping("/bills/date-range")
    @RequiresPermission("BILLING_VIEW")
    public ResponseEntity<List<Bill>> getBillsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Bill> bills = billingService.getBillsByDateRange(startDate, endDate);
        return ResponseEntity.ok(bills);
    }
    
    /**
     * 获取指定日期的总收入
     */
    @GetMapping("/revenue/{date}")
    @RequiresPermission("BILLING_VIEW")
    public ResponseEntity<Map<String, Object>> getTotalRevenueByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        BigDecimal revenue = billingService.getTotalRevenueByDate(date);
        
        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("totalRevenue", revenue);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新账单总金额
     */
    @PutMapping("/bills/{billId}/total")
    @RequiresPermission("BILLING_UPDATE")
    public ResponseEntity<Void> updateBillTotalAmount(@PathVariable Long billId) {
        billingService.updateBillTotalAmount(billId);
        return ResponseEntity.ok().build();
    }
}