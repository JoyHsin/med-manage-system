package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.me.joy.clinic.dto.AddBillItemRequest;
import org.me.joy.clinic.dto.CreateBillRequest;
import org.me.joy.clinic.entity.Bill;
import org.me.joy.clinic.entity.BillItem;
import org.me.joy.clinic.entity.Registration;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.BillItemMapper;
import org.me.joy.clinic.mapper.BillMapper;
import org.me.joy.clinic.mapper.RegistrationMapper;
import org.me.joy.clinic.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 账单管理服务实现类
 */
@Service
@Transactional
public class BillingServiceImpl implements BillingService {
    
    @Autowired
    private BillMapper billMapper;
    
    @Autowired
    private BillItemMapper billItemMapper;
    
    @Autowired
    private RegistrationMapper registrationMapper;
    
    @Override
    public Bill createBill(CreateBillRequest request) {
        // 验证患者是否存在
        if (request.getPatientId() == null) {
            throw new BusinessException("BILLING_001", "患者ID不能为空");
        }
        
        // 如果有挂号ID，验证挂号记录是否存在
        if (request.getRegistrationId() != null) {
            Registration registration = registrationMapper.selectById(request.getRegistrationId());
            if (registration == null) {
                throw new BusinessException("BILLING_002", "挂号记录不存在");
            }
            
            // 检查是否已经存在账单
            Bill existingBill = billMapper.findByRegistrationId(request.getRegistrationId());
            if (existingBill != null) {
                throw new BusinessException("BILLING_003", "该挂号记录已存在账单");
            }
        }
        
        // 创建账单
        Bill bill = new Bill();
        bill.setPatientId(request.getPatientId());
        bill.setRegistrationId(request.getRegistrationId());
        bill.setBillNumber(generateBillNumber());
        bill.setNotes(request.getNotes());
        
        billMapper.insert(bill);
        return bill;
    }
    
    @Override
    public Bill calculateBill(Long registrationId) {
        if (registrationId == null) {
            throw new BusinessException("BILLING_004", "挂号ID不能为空");
        }
        
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            throw new BusinessException("BILLING_005", "挂号记录不存在");
        }
        
        // 检查是否已存在账单
        Bill existingBill = billMapper.findByRegistrationId(registrationId);
        if (existingBill != null) {
            return existingBill;
        }
        
        // 创建新账单
        CreateBillRequest request = new CreateBillRequest();
        request.setPatientId(registration.getPatientId());
        request.setRegistrationId(registrationId);
        
        Bill bill = createBill(request);
        
        // 添加挂号费
        if (registration.getRegistrationFee() != null && registration.getRegistrationFee().compareTo(BigDecimal.ZERO) > 0) {
            AddBillItemRequest itemRequest = new AddBillItemRequest();
            itemRequest.setItemType("REGISTRATION");
            itemRequest.setItemName("挂号费");
            itemRequest.setItemCode("REG001");
            itemRequest.setUnitPrice(registration.getRegistrationFee());
            itemRequest.setQuantity(1);
            itemRequest.setSpecification("次");
            
            addBillItem(bill.getId(), itemRequest);
        }
        
        return getBillById(bill.getId());
    }
    
    @Override
    public BillItem addBillItem(Long billId, AddBillItemRequest request) {
        if (billId == null) {
            throw new BusinessException("BILLING_006", "账单ID不能为空");
        }
        
        Bill bill = billMapper.selectById(billId);
        if (bill == null) {
            throw new BusinessException("BILLING_007", "账单不存在");
        }
        
        if ("CANCELLED".equals(bill.getStatus())) {
            throw new BusinessException("BILLING_008", "已取消的账单不能添加项目");
        }
        
        // 创建账单项目
        BillItem billItem = new BillItem();
        billItem.setBillId(billId);
        billItem.setItemType(request.getItemType());
        billItem.setItemName(request.getItemName());
        billItem.setItemCode(request.getItemCode());
        billItem.setUnitPrice(request.getUnitPrice());
        billItem.setQuantity(request.getQuantity());
        billItem.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);
        billItem.setSpecification(request.getSpecification());
        billItem.setNotes(request.getNotes());
        billItem.setPrescriptionItemId(request.getPrescriptionItemId());
        billItem.setMedicalRecordId(request.getMedicalRecordId());
        
        // 计算金额
        billItem.calculateAmounts();
        
        billItemMapper.insert(billItem);
        
        // 更新账单总金额
        updateBillTotalAmount(billId);
        
        return billItem;
    }
    
    @Override
    public Bill getBillById(Long billId) {
        if (billId == null) {
            throw new BusinessException("BILLING_009", "账单ID不能为空");
        }
        
        Bill bill = billMapper.selectById(billId);
        if (bill == null) {
            throw new BusinessException("BILLING_010", "账单不存在");
        }
        
        return bill;
    }
    
    @Override
    public Bill getBillByNumber(String billNumber) {
        if (billNumber == null || billNumber.trim().isEmpty()) {
            throw new BusinessException("BILLING_011", "账单编号不能为空");
        }
        
        Bill bill = billMapper.findByBillNumber(billNumber);
        if (bill == null) {
            throw new BusinessException("BILLING_012", "账单不存在");
        }
        
        return bill;
    }
    
    @Override
    public List<Bill> getPatientBills(Long patientId) {
        if (patientId == null) {
            throw new BusinessException("BILLING_013", "患者ID不能为空");
        }
        
        return billMapper.findByPatientId(patientId);
    }
    
    @Override
    public List<BillItem> getBillItems(Long billId) {
        if (billId == null) {
            throw new BusinessException("BILLING_014", "账单ID不能为空");
        }
        
        return billItemMapper.findByBillId(billId);
    }
    
    @Override
    public void updateBillTotalAmount(Long billId) {
        if (billId == null) {
            throw new BusinessException("BILLING_014", "账单ID不能为空");
        }
        
        Bill bill = billMapper.selectById(billId);
        if (bill == null) {
            throw new BusinessException("BILLING_012", "账单不存在");
        }
        
        // 计算总金额
        BigDecimal totalAmount = billItemMapper.calculateTotalAmount(billId);
        bill.setTotalAmount(totalAmount);
        
        // 更新支付状态
        bill.updatePaymentStatus();
        
        billMapper.updateById(bill);
    }
    
    @Override
    public void updatePaymentStatus(Long billId, BigDecimal paidAmount) {
        if (billId == null) {
            throw new BusinessException("BILLING_014", "账单ID不能为空");
        }
        
        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("BILLING_015", "支付金额不能为空或负数");
        }
        
        Bill bill = billMapper.selectById(billId);
        if (bill == null) {
            throw new BusinessException("BILLING_012", "账单不存在");
        }
        
        if ("CANCELLED".equals(bill.getStatus())) {
            throw new BusinessException("BILLING_016", "已取消的账单不能更新支付状态");
        }
        
        bill.setPaidAmount(paidAmount);
        bill.updatePaymentStatus();
        
        billMapper.updateById(bill);
    }
    
    @Override
    public void cancelBill(Long billId, String reason) {
        if (billId == null) {
            throw new BusinessException("BILLING_014", "账单ID不能为空");
        }
        
        Bill bill = billMapper.selectById(billId);
        if (bill == null) {
            throw new BusinessException("BILLING_012", "账单不存在");
        }
        
        if ("PAID".equals(bill.getStatus())) {
            throw new BusinessException("BILLING_017", "已支付的账单不能取消");
        }
        
        bill.setStatus("CANCELLED");
        bill.setNotes(bill.getNotes() + (bill.getNotes() != null ? "; " : "") + "取消原因: " + reason);
        
        billMapper.updateById(bill);
    }
    
    @Override
    public List<Bill> getBillsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new BusinessException("BILLING_018", "账单状态不能为空");
        }
        
        return billMapper.findByStatus(status);
    }
    
    @Override
    public List<Bill> getBillsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessException("BILLING_019", "开始日期和结束日期不能为空");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("BILLING_020", "开始日期不能晚于结束日期");
        }
        
        return billMapper.findByDateRange(startDate, endDate);
    }
    
    @Override
    public BigDecimal getTotalRevenueByDate(LocalDate date) {
        if (date == null) {
            throw new BusinessException("BILLING_021", "日期不能为空");
        }
        
        BigDecimal revenue = billMapper.getTotalAmountByDate(date);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    @Override
    public void removeBillItem(Long billItemId) {
        if (billItemId == null) {
            throw new BusinessException("BILLING_022", "账单项目ID不能为空");
        }
        
        BillItem billItem = billItemMapper.selectById(billItemId);
        if (billItem == null) {
            throw new BusinessException("BILLING_023", "账单项目不存在");
        }
        
        Bill bill = billMapper.selectById(billItem.getBillId());
        if (bill != null && "CANCELLED".equals(bill.getStatus())) {
            throw new BusinessException("BILLING_024", "已取消的账单不能删除项目");
        }
        
        billItemMapper.deleteById(billItemId);
        
        // 更新账单总金额
        if (billItem.getBillId() != null) {
            updateBillTotalAmount(billItem.getBillId());
        }
    }
    
    @Override
    public BillItem updateBillItem(Long billItemId, AddBillItemRequest request) {
        if (billItemId == null) {
            throw new BusinessException("BILLING_022", "账单项目ID不能为空");
        }
        
        BillItem billItem = billItemMapper.selectById(billItemId);
        if (billItem == null) {
            throw new BusinessException("BILLING_023", "账单项目不存在");
        }
        
        Bill bill = billMapper.selectById(billItem.getBillId());
        if (bill != null && "CANCELLED".equals(bill.getStatus())) {
            throw new BusinessException("BILLING_025", "已取消的账单不能修改项目");
        }
        
        // 更新账单项目
        billItem.setItemType(request.getItemType());
        billItem.setItemName(request.getItemName());
        billItem.setItemCode(request.getItemCode());
        billItem.setUnitPrice(request.getUnitPrice());
        billItem.setQuantity(request.getQuantity());
        billItem.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);
        billItem.setSpecification(request.getSpecification());
        billItem.setNotes(request.getNotes());
        billItem.setPrescriptionItemId(request.getPrescriptionItemId());
        billItem.setMedicalRecordId(request.getMedicalRecordId());
        
        // 重新计算金额
        billItem.calculateAmounts();
        
        billItemMapper.updateById(billItem);
        
        // 更新账单总金额
        updateBillTotalAmount(billItem.getBillId());
        
        return billItem;
    }
    
    /**
     * 生成账单编号
     */
    private String generateBillNumber() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 查询当天已有的账单数量
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("bill_number", "BILL" + dateStr);
        Long count = billMapper.selectCount(queryWrapper);
        
        // 生成序号（4位数字，不足补0）
        String sequence = String.format("%04d", count + 1);
        
        return "BILL" + dateStr + sequence;
    }
}