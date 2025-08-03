package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.Medicine;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.InventoryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存管理控制器
 */
@RestController
@RequestMapping("/inventory")
public class InventoryManagementController {

    @Autowired
    private InventoryManagementService inventoryManagementService;

    /**
     * 创建药品
     */
    @PostMapping("/medicines")
    @RequiresPermission("MEDICINE_CREATE")
    public ResponseEntity<Medicine> createMedicine(@Valid @RequestBody CreateMedicineRequest request) {
        Medicine medicine = inventoryManagementService.createMedicine(request);
        return ResponseEntity.ok(medicine);
    }

    /**
     * 更新药品信息
     */
    @PutMapping("/medicines/{id}")
    @RequiresPermission("MEDICINE_UPDATE")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable Long id, 
                                                 @Valid @RequestBody UpdateMedicineRequest request) {
        Medicine medicine = inventoryManagementService.updateMedicine(id, request);
        return ResponseEntity.ok(medicine);
    }

    /**
     * 根据ID获取药品
     */
    @GetMapping("/medicines/{id}")
    @RequiresPermission("MEDICINE_VIEW")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable Long id) {
        Medicine medicine = inventoryManagementService.getMedicineById(id);
        return ResponseEntity.ok(medicine);
    }

    /**
     * 根据药品编码获取药品
     */
    @GetMapping("/medicines/code/{code}")
    @RequiresPermission("MEDICINE_VIEW")
    public ResponseEntity<Medicine> getMedicineByCode(@PathVariable String code) {
        Medicine medicine = inventoryManagementService.getMedicineByCode(code);
        return ResponseEntity.ok(medicine);
    }

    /**
     * 搜索药品
     */
    @PostMapping("/medicines/search")
    @RequiresPermission("MEDICINE_VIEW")
    public ResponseEntity<List<Medicine>> searchMedicines(@RequestBody MedicineSearchCriteria criteria) {
        List<Medicine> medicines = inventoryManagementService.searchMedicines(criteria);
        return ResponseEntity.ok(medicines);
    }

    /**
     * 获取所有启用的药品
     */
    @GetMapping("/medicines")
    @RequiresPermission("MEDICINE_VIEW")
    public ResponseEntity<List<Medicine>> getAllActiveMedicines() {
        List<Medicine> medicines = inventoryManagementService.getAllActiveMedicines();
        return ResponseEntity.ok(medicines);
    }

    /**
     * 根据分类获取药品
     */
    @GetMapping("/medicines/category/{category}")
    @RequiresPermission("MEDICINE_VIEW")
    public ResponseEntity<List<Medicine>> getMedicinesByCategory(@PathVariable String category) {
        List<Medicine> medicines = inventoryManagementService.getMedicinesByCategory(category);
        return ResponseEntity.ok(medicines);
    }

    /**
     * 获取处方药列表
     */
    @GetMapping("/medicines/prescription")
    @RequiresPermission("MEDICINE_VIEW")
    public ResponseEntity<List<Medicine>> getPrescriptionMedicines() {
        List<Medicine> medicines = inventoryManagementService.getPrescriptionMedicines();
        return ResponseEntity.ok(medicines);
    }

    /**
     * 获取非处方药列表
     */
    @GetMapping("/medicines/otc")
    @RequiresPermission("MEDICINE_VIEW")
    public ResponseEntity<List<Medicine>> getOverTheCounterMedicines() {
        List<Medicine> medicines = inventoryManagementService.getOverTheCounterMedicines();
        return ResponseEntity.ok(medicines);
    }

    /**
     * 获取特殊管制药品列表
     */
    @GetMapping("/medicines/controlled")
    @RequiresPermission("MEDICINE_VIEW")
    public ResponseEntity<List<Medicine>> getControlledSubstances() {
        List<Medicine> medicines = inventoryManagementService.getControlledSubstances();
        return ResponseEntity.ok(medicines);
    }

    /**
     * 更新库存
     */
    @PostMapping("/medicines/{id}/stock")
    @RequiresPermission("INVENTORY_UPDATE")
    public ResponseEntity<Map<String, String>> updateStock(@PathVariable Long id, 
                                                          @Valid @RequestBody StockUpdateRequest request) {
        inventoryManagementService.updateStock(id, request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "库存更新成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取需要补货的药品列表
     */
    @GetMapping("/medicines/low-stock")
    @RequiresPermission("INVENTORY_VIEW")
    public ResponseEntity<List<Medicine>> getLowStockMedicines() {
        List<Medicine> medicines = inventoryManagementService.getLowStockMedicines();
        return ResponseEntity.ok(medicines);
    }

    /**
     * 获取库存过多的药品列表
     */
    @GetMapping("/medicines/overstocked")
    @RequiresPermission("INVENTORY_VIEW")
    public ResponseEntity<List<Medicine>> getOverstockedMedicines() {
        List<Medicine> medicines = inventoryManagementService.getOverstockedMedicines();
        return ResponseEntity.ok(medicines);
    }

    /**
     * 获取即将过期的药品列表
     */
    @GetMapping("/medicines/expiring")
    @RequiresPermission("INVENTORY_VIEW")
    public ResponseEntity<List<Medicine>> getExpiringMedicines(@RequestParam(defaultValue = "30") int days) {
        List<Medicine> medicines = inventoryManagementService.getExpiringMedicines(days);
        return ResponseEntity.ok(medicines);
    }

    /**
     * 获取已过期的药品列表
     */
    @GetMapping("/medicines/expired")
    @RequiresPermission("INVENTORY_VIEW")
    public ResponseEntity<List<Medicine>> getExpiredMedicines() {
        List<Medicine> medicines = inventoryManagementService.getExpiredMedicines();
        return ResponseEntity.ok(medicines);
    }

    /**
     * 获取药品的当前库存总量
     */
    @GetMapping("/medicines/{id}/stock/current")
    @RequiresPermission("INVENTORY_VIEW")
    public ResponseEntity<Map<String, Integer>> getCurrentStock(@PathVariable Long id) {
        Integer stock = inventoryManagementService.getCurrentStock(id);
        Map<String, Integer> response = new HashMap<>();
        response.put("currentStock", stock);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取药品的可用库存总量
     */
    @GetMapping("/medicines/{id}/stock/available")
    @RequiresPermission("INVENTORY_VIEW")
    public ResponseEntity<Map<String, Integer>> getAvailableStock(@PathVariable Long id) {
        Integer stock = inventoryManagementService.getAvailableStock(id);
        Map<String, Integer> response = new HashMap<>();
        response.put("availableStock", stock);
        return ResponseEntity.ok(response);
    }

    /**
     * 预留库存
     */
    @PostMapping("/medicines/{id}/stock/reserve")
    @RequiresPermission("INVENTORY_UPDATE")
    public ResponseEntity<Map<String, Object>> reserveStock(@PathVariable Long id, 
                                                           @RequestParam Integer quantity) {
        boolean success = inventoryManagementService.reserveStock(id, quantity);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "库存预留成功" : "库存预留失败，可用库存不足");
        return ResponseEntity.ok(response);
    }

    /**
     * 释放预留库存
     */
    @PostMapping("/medicines/{id}/stock/release")
    @RequiresPermission("INVENTORY_UPDATE")
    public ResponseEntity<Map<String, String>> releaseReservedStock(@PathVariable Long id, 
                                                                   @RequestParam Integer quantity) {
        inventoryManagementService.releaseReservedStock(id, quantity);
        Map<String, String> response = new HashMap<>();
        response.put("message", "预留库存释放成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 启用药品
     */
    @PostMapping("/medicines/{id}/enable")
    @RequiresPermission("MEDICINE_UPDATE")
    public ResponseEntity<Map<String, String>> enableMedicine(@PathVariable Long id) {
        inventoryManagementService.enableMedicine(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "药品启用成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 禁用药品
     */
    @PostMapping("/medicines/{id}/disable")
    @RequiresPermission("MEDICINE_UPDATE")
    public ResponseEntity<Map<String, String>> disableMedicine(@PathVariable Long id) {
        inventoryManagementService.disableMedicine(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "药品禁用成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 删除药品（软删除）
     */
    @DeleteMapping("/medicines/{id}")
    @RequiresPermission("MEDICINE_DELETE")
    public ResponseEntity<Map<String, String>> deleteMedicine(@PathVariable Long id) {
        inventoryManagementService.deleteMedicine(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "药品删除成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取库存统计信息
     */
    @GetMapping("/statistics")
    @RequiresPermission("INVENTORY_VIEW")
    public ResponseEntity<Map<String, Object>> getInventoryStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 药品总数
        Long totalMedicines = inventoryManagementService.countActiveMedicines();
        statistics.put("totalMedicines", totalMedicines);
        
        // 各分类药品数量
        List<Object> categoryStats = inventoryManagementService.countMedicinesByCategory();
        statistics.put("medicinesByCategory", categoryStats);
        
        // 库存总价值
        Double totalValue = inventoryManagementService.getTotalInventoryValue();
        statistics.put("totalInventoryValue", totalValue);
        
        // 需要补货的药品数量
        List<Medicine> lowStockMedicines = inventoryManagementService.getLowStockMedicines();
        statistics.put("lowStockCount", lowStockMedicines.size());
        
        // 库存过多的药品数量
        List<Medicine> overstockedMedicines = inventoryManagementService.getOverstockedMedicines();
        statistics.put("overstockedCount", overstockedMedicines.size());
        
        // 即将过期的药品数量
        List<Medicine> expiringMedicines = inventoryManagementService.getExpiringMedicines(30);
        statistics.put("expiringCount", expiringMedicines.size());
        
        // 已过期的药品数量
        List<Medicine> expiredMedicines = inventoryManagementService.getExpiredMedicines();
        statistics.put("expiredCount", expiredMedicines.size());
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取指定药品的库存价值
     */
    @GetMapping("/medicines/{id}/value")
    @RequiresPermission("INVENTORY_VIEW")
    public ResponseEntity<Map<String, Double>> getInventoryValueByMedicine(@PathVariable Long id) {
        Double value = inventoryManagementService.getInventoryValueByMedicine(id);
        Map<String, Double> response = new HashMap<>();
        response.put("inventoryValue", value);
        return ResponseEntity.ok(response);
    }
}