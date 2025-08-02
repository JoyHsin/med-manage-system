package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.CreateMedicalOrderRequest;
import org.me.joy.clinic.dto.ExecuteMedicalOrderRequest;
import org.me.joy.clinic.entity.MedicalOrder;

import java.util.List;

public interface MedicalOrderService {
    MedicalOrder createMedicalOrder(CreateMedicalOrderRequest request, Long prescribedBy);
    List<MedicalOrder> getPendingOrders(Long patientId);
    List<MedicalOrder> getExecutedOrders(Long patientId);
    MedicalOrder executeOrder(Long orderId, ExecuteMedicalOrderRequest request, Long executedBy);
    MedicalOrder postponeOrder(Long orderId, String reason, Long postponedBy);
    MedicalOrder cancelOrder(Long orderId, String reason, Long cancelledBy);
    MedicalOrder getMedicalOrderById(Long orderId);
    List<MedicalOrder> getUrgentPendingOrders();
    List<MedicalOrder> getOrdersByNurseAndTimeRange(Long nurseId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    List<MedicalOrder> getOrdersByDoctorAndTimeRange(Long doctorId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    Long countOrdersByPatientIdAndStatus(Long patientId, String status);
}