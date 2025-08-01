package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.CreateAppointmentRequest;
import org.me.joy.clinic.dto.UpdateAppointmentRequest;
import org.me.joy.clinic.entity.Appointment;
import org.me.joy.clinic.entity.AvailableSlot;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.AppointmentMapper;
import org.me.joy.clinic.service.AppointmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 预约服务实现类
 */
@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Override
    public Appointment createAppointment(CreateAppointmentRequest request) {
        // 检查时间冲突
        if (hasTimeConflict(request.getDoctorId(), request.getAppointmentTime())) {
            throw new BusinessException("APPOINTMENT_001", "该时间段已有预约，请选择其他时间");
        }

        // 创建预约实体
        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(request, appointment);
        appointment.setStatus("已预约");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        // 保存预约
        appointmentMapper.insert(appointment);
        return appointment;
    }

    @Override
    public Appointment updateAppointment(Long appointmentId, UpdateAppointmentRequest request) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // 检查预约状态是否允许修改
        if (!"已预约".equals(appointment.getStatus()) && !"已确认".equals(appointment.getStatus())) {
            throw new BusinessException("APPOINTMENT_002", "当前状态不允许修改预约");
        }

        // 如果修改了预约时间，需要检查时间冲突
        if (request.getAppointmentTime() != null && 
            !request.getAppointmentTime().equals(appointment.getAppointmentTime())) {
            if (hasTimeConflict(appointment.getDoctorId(), request.getAppointmentTime())) {
                throw new BusinessException("APPOINTMENT_001", "该时间段已有预约，请选择其他时间");
            }
            appointment.setAppointmentTime(request.getAppointmentTime());
        }

        // 更新其他字段
        if (StringUtils.hasText(request.getAppointmentType())) {
            appointment.setAppointmentType(request.getAppointmentType());
        }
        if (StringUtils.hasText(request.getDepartment())) {
            appointment.setDepartment(request.getDepartment());
        }
        if (StringUtils.hasText(request.getChiefComplaint())) {
            appointment.setChiefComplaint(request.getChiefComplaint());
        }
        if (StringUtils.hasText(request.getNotes())) {
            appointment.setNotes(request.getNotes());
        }
        if (request.getPriority() != null) {
            appointment.setPriority(request.getPriority());
        }
        if (request.getAppointmentFee() != null) {
            appointment.setAppointmentFee(request.getAppointmentFee());
        }
        if (request.getNeedReminder() != null) {
            appointment.setNeedReminder(request.getNeedReminder());
        }
        if (request.getReminderMinutes() != null) {
            appointment.setReminderMinutes(request.getReminderMinutes());
        }

        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentMapper.updateById(appointment);
        return appointment;
    }

    @Override
    public Appointment getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("APPOINTMENT_003", "预约不存在，ID: " + appointmentId);
        }
        return appointment;
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentMapper.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentMapper.findByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentMapper.findByDate(date);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        return appointmentMapper.findByDoctorIdAndDate(doctorId, date);
    }

    @Override
    public List<AvailableSlot> getAvailableSlots(Long doctorId, LocalDate date) {
        // 获取已预约的时间段
        List<LocalDateTime> bookedSlots = appointmentMapper.findBookedTimeSlots(doctorId, date);
        
        // 生成可用时间段（这里简化处理，实际应该根据医生排班）
        List<AvailableSlot> availableSlots = new ArrayList<>();
        
        // 上午时段：8:00-12:00，每30分钟一个时段
        LocalDateTime startTime = date.atTime(8, 0);
        LocalDateTime endTime = date.atTime(12, 0);
        
        while (startTime.isBefore(endTime)) {
            LocalDateTime slotEnd = startTime.plusMinutes(30);
            boolean isAvailable = !bookedSlots.contains(startTime);
            
            AvailableSlot slot = new AvailableSlot(startTime, slotEnd, isAvailable);
            slot.setDoctorId(doctorId);
            availableSlots.add(slot);
            
            startTime = slotEnd;
        }
        
        // 下午时段：14:00-18:00，每30分钟一个时段
        startTime = date.atTime(14, 0);
        endTime = date.atTime(18, 0);
        
        while (startTime.isBefore(endTime)) {
            LocalDateTime slotEnd = startTime.plusMinutes(30);
            boolean isAvailable = !bookedSlots.contains(startTime);
            
            AvailableSlot slot = new AvailableSlot(startTime, slotEnd, isAvailable);
            slot.setDoctorId(doctorId);
            availableSlots.add(slot);
            
            startTime = slotEnd;
        }
        
        return availableSlots;
    }

    @Override
    public boolean hasTimeConflict(Long doctorId, LocalDateTime appointmentTime) {
        Integer count = appointmentMapper.checkTimeConflict(doctorId, appointmentTime);
        return count != null && count > 0;
    }

    @Override
    public void confirmAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.confirm();
        appointmentMapper.updateById(appointment);
    }

    @Override
    public void cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.cancel(reason);
        appointmentMapper.updateById(appointment);
    }

    @Override
    public void markPatientArrived(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.markArrived();
        appointmentMapper.updateById(appointment);
    }

    @Override
    public void startConsultation(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.start();
        appointmentMapper.updateById(appointment);
    }

    @Override
    public void completeConsultation(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.complete();
        appointmentMapper.updateById(appointment);
    }

    @Override
    public void markNoShow(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.markNoShow();
        appointmentMapper.updateById(appointment);
    }

    @Override
    public List<Appointment> getTodayAppointments() {
        return appointmentMapper.findTodayAppointments();
    }

    @Override
    public List<Appointment> getTomorrowAppointments() {
        return appointmentMapper.findTomorrowAppointments();
    }

    @Override
    public List<Appointment> getAppointmentsNeedingReminder() {
        return appointmentMapper.findAppointmentsNeedingReminder();
    }

    @Override
    public List<Appointment> getExpiredAppointments() {
        return appointmentMapper.findExpiredAppointments();
    }

    @Override
    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentMapper.findByStatus(status);
    }

    @Override
    public List<Appointment> getAppointmentsByType(String appointmentType) {
        return appointmentMapper.findByAppointmentType(appointmentType);
    }

    @Override
    public List<Appointment> getAppointmentsByDepartment(String department) {
        return appointmentMapper.findByDepartment(department);
    }

    @Override
    public List<Appointment> getHighPriorityAppointments() {
        return appointmentMapper.findHighPriorityAppointments();
    }

    @Override
    public Long countAppointmentsByDate(LocalDate date) {
        return appointmentMapper.countAppointmentsByDate(date);
    }

    @Override
    public Long countAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        return appointmentMapper.countAppointmentsByDoctorAndDate(doctorId, date);
    }

    @Override
    public List<Object> countAppointmentsByStatus() {
        return appointmentMapper.countAppointmentsByStatus();
    }

    @Override
    public List<Object> countAppointmentsByType() {
        return appointmentMapper.countAppointmentsByType();
    }

    @Override
    public Appointment getLatestAppointmentByPatient(Long patientId) {
        return appointmentMapper.findLatestByPatientId(patientId);
    }

    @Override
    public Appointment getNextAppointmentByDoctor(Long doctorId) {
        return appointmentMapper.findNextByDoctorId(doctorId);
    }

    @Override
    public void processExpiredAppointments() {
        List<Appointment> expiredAppointments = getExpiredAppointments();
        for (Appointment appointment : expiredAppointments) {
            appointment.markNoShow();
            appointmentMapper.updateById(appointment);
        }
    }

    @Override
    public void sendAppointmentReminders() {
        List<Appointment> appointmentsNeedingReminder = getAppointmentsNeedingReminder();
        for (Appointment appointment : appointmentsNeedingReminder) {
            // 这里应该调用消息服务发送提醒
            // messageService.sendAppointmentReminder(appointment);
            System.out.println("发送预约提醒: 患者ID=" + appointment.getPatientId() + 
                             ", 预约时间=" + appointment.getAppointmentTime());
        }
    }
}