package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.CreateMedicalOrderRequest;
import org.me.joy.clinic.dto.ExecuteMedicalOrderRequest;
import org.me.joy.clinic.entity.MedicalOrder;
import org.me.joy.clinic.mapper.MedicalOrderMapper;
import org.me.joy.clinic.service.MedicalOrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalOrderServiceImpl implements MedicalOrderService {

    private final MedicalOrderMapper medicalOrderMapper;

    public MedicalOrderServiceImpl(MedicalOrderMapper medicalOrderMapper) {
        this.medicalOrderMapper = medicalOrderMapper;
    }

    @Override
    public MedicalOrder createMedicalOrder(CreateMedicalOrderRequest request, Long prescribedBy) {
        MedicalOrder medicalOrder = new MedicalOrder();
        medicalOrder.setPatientId(request.getPatientId());
        medicalOrder.setOrderType(request.getOrderType());
        medicalOrder.setContent(request.getContent());
        medicalOrder.setDosage(request.getDosage());
        medicalOrder.setFrequency(request.getFrequency());
        medicalOrder.setRoute(request.getRoute());
        medicalOrder.setNotes(request.getNotes());
        medicalOrder.setPriority(request.getPriority());
        medicalOrder.setPrice(request.getPrice());
        medicalOrder.setQuantity(request.getQuantity());
        medicalOrder.setUnit(request.getUnit());
        medicalOrder.setPrescribedBy(prescribedBy);
        medicalOrder.setPrescribedAt(LocalDateTime.now());
        medicalOrder.setStatus("PENDING");
        medicalOrderMapper.insert(medicalOrder);
        return medicalOrder;
    }

    @Override
    public List<MedicalOrder> getPendingOrders(Long patientId) {
        return medicalOrderMapper.selectList(null); // TODO: Add query conditions
    }

    @Override
    public List<MedicalOrder> getExecutedOrders(Long patientId) {
        return medicalOrderMapper.selectList(null); // TODO: Add query conditions
    }

    @Override
    public MedicalOrder executeOrder(Long orderId, ExecuteMedicalOrderRequest request, Long executedBy) {
        MedicalOrder medicalOrder = medicalOrderMapper.selectById(orderId);
        if (medicalOrder == null) {
            throw new RuntimeException("Medical order not found");
        }
        medicalOrder.setStatus("EXECUTED");
        medicalOrder.setExecutedBy(executedBy);
        medicalOrder.setExecutedAt(LocalDateTime.now());
        medicalOrder.setExecutionNotes(request.getExecutionNotes());
        medicalOrderMapper.updateById(medicalOrder);
        return medicalOrder;
    }

    @Override
    public MedicalOrder postponeOrder(Long orderId, String reason, Long postponedBy) {
        MedicalOrder medicalOrder = medicalOrderMapper.selectById(orderId);
        if (medicalOrder == null) {
            throw new RuntimeException("Medical order not found");
        }
        medicalOrder.setStatus("POSTPONED");
        medicalOrder.setPostponeReason(reason);
        medicalOrder.setPostponedBy(postponedBy);
        medicalOrder.setPostponedAt(LocalDateTime.now());
        medicalOrderMapper.updateById(medicalOrder);
        return medicalOrder;
    }

    @Override
    public MedicalOrder cancelOrder(Long orderId, String reason, Long cancelledBy) {
        MedicalOrder medicalOrder = medicalOrderMapper.selectById(orderId);
        if (medicalOrder == null) {
            throw new RuntimeException("Medical order not found");
        }
        medicalOrder.setStatus("CANCELLED");
        medicalOrder.setCancelReason(reason);
        medicalOrder.setCancelledBy(cancelledBy);
        medicalOrder.setCancelledAt(LocalDateTime.now());
        medicalOrderMapper.updateById(medicalOrder);
        return medicalOrder;
    }

    @Override
    public MedicalOrder getMedicalOrderById(Long orderId) {
        return medicalOrderMapper.selectById(orderId);
    }

    @Override
    public List<MedicalOrder> getUrgentPendingOrders() {
        // Implement logic to get urgent pending orders
        return medicalOrderMapper.selectList(null); // Placeholder
    }

    @Override
    public List<MedicalOrder> getOrdersByNurseAndTimeRange(Long nurseId, LocalDateTime startTime, LocalDateTime endTime) {
        // Implement logic to get orders by nurse and time range
        return medicalOrderMapper.selectList(null); // Placeholder
    }

    @Override
    public List<MedicalOrder> getOrdersByDoctorAndTimeRange(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        // Implement logic to get orders by doctor and time range
        return medicalOrderMapper.selectList(null); // Placeholder
    }

    @Override
    public Long countOrdersByPatientIdAndStatus(Long patientId, String status) {
        // Implement logic to count orders by patient ID and status
        return medicalOrderMapper.selectCount(null); // Placeholder
    }
}