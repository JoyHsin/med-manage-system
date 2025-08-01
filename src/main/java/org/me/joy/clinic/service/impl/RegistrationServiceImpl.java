package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.CreateRegistrationRequest;
import org.me.joy.clinic.entity.Appointment;
import org.me.joy.clinic.entity.Registration;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.AppointmentMapper;
import org.me.joy.clinic.mapper.RegistrationMapper;
import org.me.joy.clinic.service.RegistrationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 挂号服务实现类
 */
@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Override
    public Registration createRegistration(CreateRegistrationRequest request) {
        // 生成挂号编号
        String registrationNumber = generateRegistrationNumber();
        
        // 获取队列号
        Integer queueNumber = registrationMapper.getNextQueueNumber(
            request.getRegistrationDate(), request.getDepartment());
        
        // 创建挂号实体
        Registration registration = new Registration();
        BeanUtils.copyProperties(request, registration);
        registration.setRegistrationNumber(registrationNumber);
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setQueueNumber(queueNumber);
        registration.setStatus("已挂号");
        registration.setPaymentStatus("未支付");
        
        // 计算总费用
        registration.calculateTotalFee();
        
        // 如果指定了支付方式，标记为已支付
        if (request.getPaymentMethod() != null) {
            registration.setPaymentStatus("已支付");
            registration.setPaymentMethod(request.getPaymentMethod());
        }
        
        registration.setCreatedAt(LocalDateTime.now());
        registration.setUpdatedAt(LocalDateTime.now());

        // 保存挂号记录
        registrationMapper.insert(registration);
        return registration;
    }

    @Override
    public Registration getRegistrationById(Long registrationId) {
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            throw new BusinessException("REGISTRATION_001", "挂号记录不存在，ID: " + registrationId);
        }
        return registration;
    }

    @Override
    public Registration getRegistrationByNumber(String registrationNumber) {
        Registration registration = registrationMapper.findByRegistrationNumber(registrationNumber);
        if (registration == null) {
            throw new BusinessException("REGISTRATION_002", "挂号记录不存在，编号: " + registrationNumber);
        }
        return registration;
    }

    @Override
    public List<Registration> getRegistrationsByPatientId(Long patientId) {
        return registrationMapper.findByPatientId(patientId);
    }

    @Override
    public Registration getRegistrationByAppointmentId(Long appointmentId) {
        return registrationMapper.findByAppointmentId(appointmentId);
    }

    @Override
    public List<Registration> getRegistrationsByDate(LocalDate date) {
        return registrationMapper.findByRegistrationDate(date);
    }

    @Override
    public List<Registration> getRegistrationsByDepartment(String department) {
        return registrationMapper.findByDepartment(department);
    }

    @Override
    public List<Registration> getRegistrationsByDoctorId(Long doctorId) {
        return registrationMapper.findByDoctorId(doctorId);
    }

    @Override
    public List<Registration> getTodayRegistrations() {
        return registrationMapper.findTodayRegistrations();
    }

    @Override
    public List<Registration> getTodayRegistrationsByDepartment(String department) {
        return registrationMapper.findTodayRegistrationsByDepartment(department);
    }

    @Override
    public List<Registration> getTodayRegistrationsByDoctor(Long doctorId) {
        return registrationMapper.findTodayRegistrationsByDoctor(doctorId);
    }

    @Override
    public List<Registration> getPendingRegistrations() {
        return registrationMapper.findPendingRegistrations();
    }

    @Override
    public List<Registration> getPendingRegistrationsByDepartment(String department) {
        return registrationMapper.findPendingRegistrationsByDepartment(department);
    }

    @Override
    public List<Registration> getPendingRegistrationsByDoctor(Long doctorId) {
        return registrationMapper.findPendingRegistrationsByDoctor(doctorId);
    }

    @Override
    public List<Registration> getCalledButNotArrivedRegistrations() {
        return registrationMapper.findCalledButNotArrivedRegistrations();
    }

    @Override
    public List<Registration> getActiveConsultations() {
        return registrationMapper.findActiveConsultations();
    }

    @Override
    public void callPatient(Long registrationId) {
        Registration registration = getRegistrationById(registrationId);
        registration.call();
        registrationMapper.updateById(registration);
    }

    @Override
    public void markPatientArrived(Long registrationId) {
        Registration registration = getRegistrationById(registrationId);
        registration.markArrived();
        registrationMapper.updateById(registration);
    }

    @Override
    public void startConsultation(Long registrationId) {
        Registration registration = getRegistrationById(registrationId);
        registration.startConsultation();
        registrationMapper.updateById(registration);
    }

    @Override
    public void completeConsultation(Long registrationId) {
        Registration registration = getRegistrationById(registrationId);
        registration.complete();
        registrationMapper.updateById(registration);
    }

    @Override
    public void cancelRegistration(Long registrationId, String reason) {
        Registration registration = getRegistrationById(registrationId);
        registration.cancel(reason);
        registrationMapper.updateById(registration);
    }

    @Override
    public void markNoShow(Long registrationId) {
        Registration registration = getRegistrationById(registrationId);
        registration.markNoShow();
        registrationMapper.updateById(registration);
    }

    @Override
    public void markAsPaid(Long registrationId, String paymentMethod) {
        Registration registration = getRegistrationById(registrationId);
        registration.markPaid(paymentMethod);
        registrationMapper.updateById(registration);
    }

    @Override
    public void updateRegistrationStatus(Long registrationId, String status) {
        Registration registration = getRegistrationById(registrationId);
        registration.setStatus(status);
        registration.setUpdatedAt(LocalDateTime.now());
        registrationMapper.updateById(registration);
    }

    @Override
    public List<Registration> getRegistrationsByStatus(String status) {
        return registrationMapper.findByStatus(status);
    }

    @Override
    public List<Registration> getRegistrationsByPaymentStatus(String paymentStatus) {
        return registrationMapper.findByPaymentStatus(paymentStatus);
    }

    @Override
    public List<Registration> getEmergencyRegistrations() {
        return registrationMapper.findEmergencyRegistrations();
    }

    @Override
    public List<Registration> getFirstVisitRegistrations() {
        return registrationMapper.findFirstVisitRegistrations();
    }

    @Override
    public List<Registration> getUnpaidRegistrations() {
        return registrationMapper.findUnpaidRegistrations();
    }

    @Override
    public List<Registration> getCompletedRegistrations() {
        return registrationMapper.findCompletedRegistrations();
    }

    @Override
    public Long countRegistrationsByDate(LocalDate date) {
        return registrationMapper.countRegistrationsByDate(date);
    }

    @Override
    public Long countRegistrationsByDateAndDepartment(LocalDate date, String department) {
        return registrationMapper.countRegistrationsByDateAndDepartment(date, department);
    }

    @Override
    public Long countRegistrationsByDateAndDoctor(LocalDate date, Long doctorId) {
        return registrationMapper.countRegistrationsByDateAndDoctor(date, doctorId);
    }

    @Override
    public List<Object> countRegistrationsByStatus() {
        return registrationMapper.countRegistrationsByStatus();
    }

    @Override
    public List<Object> countRegistrationsByDepartment() {
        return registrationMapper.countRegistrationsByDepartment();
    }

    @Override
    public List<Object> countRegistrationsByType() {
        return registrationMapper.countRegistrationsByType();
    }

    @Override
    public List<Object> countRegistrationsByPaymentStatus() {
        return registrationMapper.countRegistrationsByPaymentStatus();
    }

    @Override
    public Registration getLatestRegistrationByPatient(Long patientId) {
        return registrationMapper.findLatestByPatientId(patientId);
    }

    @Override
    public Registration createRegistrationFromAppointment(Long appointmentId) {
        // 获取预约信息
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("APPOINTMENT_003", "预约不存在，ID: " + appointmentId);
        }
        
        // 检查预约状态
        if (!"已确认".equals(appointment.getStatus()) && !"已到达".equals(appointment.getStatus())) {
            throw new BusinessException("APPOINTMENT_004", "预约状态不允许转为挂号");
        }
        
        // 检查是否已经有对应的挂号记录
        Registration existingRegistration = registrationMapper.findByAppointmentId(appointmentId);
        if (existingRegistration != null) {
            throw new BusinessException("REGISTRATION_003", "该预约已经转为挂号");
        }
        
        // 生成挂号编号
        String registrationNumber = generateRegistrationNumber();
        
        // 获取队列号
        LocalDate registrationDate = appointment.getAppointmentTime().toLocalDate();
        Integer queueNumber = registrationMapper.getNextQueueNumber(registrationDate, appointment.getDepartment());
        
        // 创建挂号记录
        Registration registration = new Registration();
        registration.setPatientId(appointment.getPatientId());
        registration.setAppointmentId(appointmentId);
        registration.setRegistrationNumber(registrationNumber);
        registration.setRegistrationDate(registrationDate);
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setDepartment(appointment.getDepartment());
        registration.setDoctorId(appointment.getDoctorId());
        registration.setRegistrationType(appointment.getAppointmentType());
        registration.setQueueNumber(queueNumber);
        registration.setPriority(appointment.getPriority());
        registration.setRegistrationFee(appointment.getAppointmentFee());
        registration.setChiefComplaint(appointment.getChiefComplaint());
        registration.setSource("预约");
        registration.setStatus("已挂号");
        registration.setPaymentStatus("未支付");
        registration.setRemarks(appointment.getNotes());
        registration.setCreatedAt(LocalDateTime.now());
        registration.setUpdatedAt(LocalDateTime.now());
        
        // 计算总费用
        registration.calculateTotalFee();
        
        // 保存挂号记录
        registrationMapper.insert(registration);
        
        // 更新预约状态
        appointment.setStatus("已转挂号");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentMapper.updateById(appointment);
        
        return registration;
    }

    @Override
    public String generateRegistrationNumber() {
        // 生成格式：REG + 年月日 + 3位序号
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 获取当日已有的挂号数量
        Long count = countRegistrationsByDate(LocalDate.now());
        String sequence = String.format("%03d", count + 1);
        
        return "REG" + dateStr + sequence;
    }
}