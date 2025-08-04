package org.me.joy.clinic.dto;

import java.math.BigDecimal;

/**
 * 支付方式统计数据传输对象
 */
public class PaymentMethodSummary {
    private String paymentMethod;
    private BigDecimal totalAmount;
    private Integer transactionCount;
    private BigDecimal averageAmount;
    private BigDecimal percentage;

    public PaymentMethodSummary() {}

    public PaymentMethodSummary(String paymentMethod, BigDecimal totalAmount, Integer transactionCount) {
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
        this.averageAmount = transactionCount > 0 ? 
            totalAmount.divide(BigDecimal.valueOf(transactionCount), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;
    }

    // Getters and Setters
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    public BigDecimal getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(BigDecimal averageAmount) {
        this.averageAmount = averageAmount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}