package org.me.joy.clinic.controller;

import org.me.joy.clinic.dto.CreateMedicalOrderRequest;
import org.me.joy.clinic.dto.ExecuteMedicalOrderRequest;
import org.me.joy.clinic.entity.MedicalOrder;
import org.me.joy.clinic.security.CustomUserPrincipal;
import org.me.joy.clinic.service.MedicalOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/medical-orders")
public class MedicalOrderController {

    private final MedicalOrderService medicalOrderService;

    public MedicalOrderController(MedicalOrderService medicalOrderService) {
        this.medicalOrderService = medicalOrderService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MEDICAL_ORDER_CREATE')")
    public ResponseEntity<MedicalOrder> createMedicalOrder(
            @Valid @RequestBody CreateMedicalOrderRequest request,
            @AuthenticationPrincipal CustomUserPrincipal currentUser) {
        MedicalOrder medicalOrder = medicalOrderService.createMedicalOrder(request, currentUser.getUserId());
        return ResponseEntity.ok(medicalOrder);
    }

    @GetMapping("/patient/{patientId}/pending")
    @PreAuthorize("hasAuthority('MEDICAL_ORDER_READ')")
    public ResponseEntity<List<MedicalOrder>> getPendingOrders(@PathVariable Long patientId) {
        List<MedicalOrder> orders = medicalOrderService.getPendingOrders(patientId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/patient/{patientId}/executed")
    @PreAuthorize("hasAuthority('MEDICAL_ORDER_READ')")
    public ResponseEntity<List<MedicalOrder>> getExecutedOrders(@PathVariable Long patientId) {
        List<MedicalOrder> orders = medicalOrderService.getExecutedOrders(patientId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderId}/execute")
    @PreAuthorize("hasAuthority('MEDICAL_ORDER_EXECUTE')")
    public ResponseEntity<MedicalOrder> executeOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody ExecuteMedicalOrderRequest request,
            @AuthenticationPrincipal CustomUserPrincipal currentUser) {
        MedicalOrder medicalOrder = medicalOrderService.executeOrder(orderId, request, currentUser.getUserId());
        return ResponseEntity.ok(medicalOrder);
    }

    @PostMapping("/{orderId}/postpone")
    @PreAuthorize("hasAuthority('MEDICAL_ORDER_EXECUTE')")
    public ResponseEntity<MedicalOrder> postponeOrder(
            @PathVariable Long orderId,
            @RequestBody String reason,
            @AuthenticationPrincipal CustomUserPrincipal currentUser) {
        MedicalOrder medicalOrder = medicalOrderService.postponeOrder(orderId, reason, currentUser.getUserId());
        return ResponseEntity.ok(medicalOrder);
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('MEDICAL_ORDER_CANCEL')")
    public ResponseEntity<MedicalOrder> cancelOrder(
            @PathVariable Long orderId,
            @RequestBody String reason,
            @AuthenticationPrincipal CustomUserPrincipal currentUser) {
        MedicalOrder medicalOrder = medicalOrderService.cancelOrder(orderId, reason, currentUser.getUserId());
        return ResponseEntity.ok(medicalOrder);
    }
}