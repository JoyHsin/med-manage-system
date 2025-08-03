package org.me.joy.clinic.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存盘点请求DTO
 */
public class StockTakingRequest {

    /**
     * 盘点日期
     */
    @NotNull(message = "盘点日期不能为空")
    private LocalDate inventoryDate;

    /**
     * 盘点类型
     */
    @NotBlank(message = "盘点类型不能为空")
    @Pattern(regexp = "^(全盘|抽盘|循环盘点|专项盘点)$", 
             message = "盘点类型只能是全盘、抽盘、循环盘点或专项盘点")
    private String inventoryType;

    /**
     * 盘点范围
     */
    @Size(max = 200, message = "盘点范围长度不能超过200个字符")
    private String inventoryScope;

    /**
     * 盘点人员
     */
    @NotBlank(message = "盘点人员不能为空")
    @Size(max = 100, message = "盘点人员长度不能超过100个字符")
    private String inventoryStaff;

    /**
     * 监盘人员
     */
    @Size(max = 100, message = "监盘人员长度不能超过100个字符")
    private String supervisor;

    /**
     * 盘点项目列表
     */
    @NotEmpty(message = "盘点项目不能为空")
    @Valid
    private List<StockTakingItem> items;

    /**
     * 盘点说明
     */
    @Size(max = 500, message = "盘点说明长度不能超过500个字符")
    private String description;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;

    // 构造函数
    public StockTakingRequest() {}

    public StockTakingRequest(LocalDate inventoryDate, String inventoryType, String inventoryStaff, List<StockTakingItem> items) {
        this.inventoryDate = inventoryDate;
        this.inventoryType = inventoryType;
        this.inventoryStaff = inventoryStaff;
        this.items = items;
    }

    // Getters and Setters
    public LocalDate getInventoryDate() {
        return inventoryDate;
    }

    public void setInventoryDate(LocalDate inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public String getInventoryScope() {
        return inventoryScope;
    }

    public void setInventoryScope(String inventoryScope) {
        this.inventoryScope = inventoryScope;
    }

    public String getInventoryStaff() {
        return inventoryStaff;
    }

    public void setInventoryStaff(String inventoryStaff) {
        this.inventoryStaff = inventoryStaff;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public List<StockTakingItem> getItems() {
        return items;
    }

    public void setItems(List<StockTakingItem> items) {
        this.items = items;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 盘点项目内部类
     */
    public static class StockTakingItem {

        /**
         * 药品ID
         */
        @NotNull(message = "药品ID不能为空")
        private Long medicineId;

        /**
         * 批次号
         */
        @NotBlank(message = "批次号不能为空")
        @Size(max = 100, message = "批次号长度不能超过100个字符")
        private String batchNumber;

        /**
         * 账面库存
         */
        @NotNull(message = "账面库存不能为空")
        @Min(value = 0, message = "账面库存不能为负数")
        private Integer bookStock;

        /**
         * 实际库存
         */
        @NotNull(message = "实际库存不能为空")
        @Min(value = 0, message = "实际库存不能为负数")
        private Integer actualStock;

        /**
         * 盘点差异
         */
        private Integer difference;

        /**
         * 差异原因
         */
        @Size(max = 200, message = "差异原因长度不能超过200个字符")
        private String differenceReason;

        /**
         * 存储位置
         */
        @Size(max = 100, message = "存储位置长度不能超过100个字符")
        private String storageLocation;

        /**
         * 备注
         */
        @Size(max = 200, message = "备注长度不能超过200个字符")
        private String remarks;

        // 构造函数
        public StockTakingItem() {}

        public StockTakingItem(Long medicineId, String batchNumber, Integer bookStock, Integer actualStock) {
            this.medicineId = medicineId;
            this.batchNumber = batchNumber;
            this.bookStock = bookStock;
            this.actualStock = actualStock;
            this.difference = actualStock - bookStock;
        }

        // Getters and Setters
        public Long getMedicineId() {
            return medicineId;
        }

        public void setMedicineId(Long medicineId) {
            this.medicineId = medicineId;
        }

        public String getBatchNumber() {
            return batchNumber;
        }

        public void setBatchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
        }

        public Integer getBookStock() {
            return bookStock;
        }

        public void setBookStock(Integer bookStock) {
            this.bookStock = bookStock;
            calculateDifference();
        }

        public Integer getActualStock() {
            return actualStock;
        }

        public void setActualStock(Integer actualStock) {
            this.actualStock = actualStock;
            calculateDifference();
        }

        public Integer getDifference() {
            return difference;
        }

        public void setDifference(Integer difference) {
            this.difference = difference;
        }

        public String getDifferenceReason() {
            return differenceReason;
        }

        public void setDifferenceReason(String differenceReason) {
            this.differenceReason = differenceReason;
        }

        public String getStorageLocation() {
            return storageLocation;
        }

        public void setStorageLocation(String storageLocation) {
            this.storageLocation = storageLocation;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        /**
         * 计算盘点差异
         */
        private void calculateDifference() {
            if (actualStock != null && bookStock != null) {
                this.difference = actualStock - bookStock;
            }
        }

        /**
         * 检查是否有差异
         */
        public boolean hasDifference() {
            return difference != null && difference != 0;
        }

        @Override
        public String toString() {
            return "StockTakingItem{" +
                    "medicineId=" + medicineId +
                    ", batchNumber='" + batchNumber + '\'' +
                    ", bookStock=" + bookStock +
                    ", actualStock=" + actualStock +
                    ", difference=" + difference +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "StockTakingRequest{" +
                "inventoryDate=" + inventoryDate +
                ", inventoryType='" + inventoryType + '\'' +
                ", inventoryStaff='" + inventoryStaff + '\'' +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}