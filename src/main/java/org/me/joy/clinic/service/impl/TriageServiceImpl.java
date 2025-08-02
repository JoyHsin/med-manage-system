package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.me.joy.clinic.entity.PatientQueue;
import org.me.joy.clinic.entity.Registration;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.PatientQueueMapper;
import org.me.joy.clinic.mapper.RegistrationMapper;
import org.me.joy.clinic.service.TriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * 分诊叫号服务实现
 */
@Service
public class TriageServiceImpl implements TriageService {

    private static final Logger log = Logger.getLogger(TriageServiceImpl.class.getName());

    private final PatientQueueMapper patientQueueMapper;
    private final RegistrationMapper registrationMapper;

    @Autowired
    public TriageServiceImpl(PatientQueueMapper patientQueueMapper, RegistrationMapper registrationMapper) {
        this.patientQueueMapper = patientQueueMapper;
        this.registrationMapper = registrationMapper;
    }

    @Override
    public List<PatientQueue> getTodayPatientQueue() {
        return getPatientQueueByDate(LocalDate.now());
    }

    @Override
    public List<PatientQueue> getPatientQueueByDate(LocalDate queueDate) {
        log.info("获取日期 " + queueDate + " 的患者队列");
        return patientQueueMapper.findByQueueDate(queueDate);
    }

    @Override
    @Transactional
    public void callPatient(Long patientQueueId, Long calledBy) {
        log.info("叫号患者，队列ID: " + patientQueueId + ", 叫号护士: " + calledBy);
        
        PatientQueue patientQueue = patientQueueMapper.selectById(patientQueueId);
        if (patientQueue == null) {
            throw new BusinessException("4001", "患者队列记录不存在");
        }

        if (!"WAITING".equals(patientQueue.getStatus()) && !"ABSENT".equals(patientQueue.getStatus())) {
            throw new BusinessException("4002", "患者状态不允许叫号");
        }

        patientQueue.setStatus("CALLED");
        patientQueue.setCalledAt(LocalDateTime.now());
        patientQueue.setCalledBy(calledBy);
        patientQueue.setCallCount(patientQueue.getCallCount() == null ? 1 : patientQueue.getCallCount() + 1);

        patientQueueMapper.updateById(patientQueue);
        log.info("患者叫号成功，队列ID: " + patientQueueId);
    }

    @Override
    @Transactional
    public void confirmPatientArrival(Long patientQueueId, Long confirmedBy) {
        log.info("确认患者到达，队列ID: " + patientQueueId + ", 确认护士: " + confirmedBy);
        
        PatientQueue patientQueue = patientQueueMapper.selectById(patientQueueId);
        if (patientQueue == null) {
            throw new BusinessException("4001", "患者队列记录不存在");
        }

        if (!"CALLED".equals(patientQueue.getStatus())) {
            throw new BusinessException("4003", "患者状态不允许确认到达");
        }

        patientQueue.setStatus("ARRIVED");
        patientQueue.setArrivedAt(LocalDateTime.now());
        patientQueue.setConfirmedBy(confirmedBy);

        patientQueueMapper.updateById(patientQueue);
        log.info("患者到达确认成功，队列ID: " + patientQueueId);
    }

    @Override
    @Transactional
    public void markPatientAbsent(Long patientQueueId, Long calledBy) {
        log.info("标记患者未到，队列ID: " + patientQueueId + ", 操作护士: " + calledBy);
        
        PatientQueue patientQueue = patientQueueMapper.selectById(patientQueueId);
        if (patientQueue == null) {
            throw new BusinessException("4001", "患者队列记录不存在");
        }

        if (!"CALLED".equals(patientQueue.getStatus())) {
            throw new BusinessException("4004", "患者状态不允许标记未到");
        }

        patientQueue.setStatus("ABSENT");
        patientQueue.setCalledBy(calledBy);

        patientQueueMapper.updateById(patientQueue);
        log.info("患者未到标记成功，队列ID: " + patientQueueId);
    }

    @Override
    public PatientQueue getNextPatient() {
        return getNextPatient(LocalDate.now());
    }

    @Override
    public PatientQueue getNextPatient(LocalDate queueDate) {
        log.info("获取下一个待叫号患者，日期: " + queueDate);
        return patientQueueMapper.findNextPatient(queueDate);
    }

    @Override
    @Transactional
    public void recallPatient(Long patientQueueId, Long calledBy) {
        log.info("重新叫号患者，队列ID: " + patientQueueId + ", 叫号护士: " + calledBy);
        
        PatientQueue patientQueue = patientQueueMapper.selectById(patientQueueId);
        if (patientQueue == null) {
            throw new BusinessException("4001", "患者队列记录不存在");
        }

        if (!"ABSENT".equals(patientQueue.getStatus())) {
            throw new BusinessException("4005", "只能重新叫号未到的患者");
        }

        patientQueue.setStatus("CALLED");
        patientQueue.setCalledAt(LocalDateTime.now());
        patientQueue.setCalledBy(calledBy);
        patientQueue.setCallCount(patientQueue.getCallCount() == null ? 1 : patientQueue.getCallCount() + 1);

        patientQueueMapper.updateById(patientQueue);
        log.info("患者重新叫号成功，队列ID: " + patientQueueId);
    }

    @Override
    @Transactional
    public void completePatient(Long patientQueueId) {
        log.info("完成患者就诊，队列ID: " + patientQueueId);
        
        PatientQueue patientQueue = patientQueueMapper.selectById(patientQueueId);
        if (patientQueue == null) {
            throw new BusinessException("4001", "患者队列记录不存在");
        }

        if (!"ARRIVED".equals(patientQueue.getStatus())) {
            throw new BusinessException("4006", "患者状态不允许完成就诊");
        }

        patientQueue.setStatus("COMPLETED");
        patientQueue.setCompletedAt(LocalDateTime.now());

        patientQueueMapper.updateById(patientQueue);
        log.info("患者就诊完成，队列ID: " + patientQueueId);
    }

    @Override
    @Transactional
    public PatientQueue createPatientQueue(Long registrationId, Integer priority) {
        log.info("创建患者队列记录，挂号ID: " + registrationId + ", 优先级: " + priority);
        
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            throw new BusinessException("4007", "挂号记录不存在");
        }

        LocalDate queueDate = registration.getRegistrationDate();
        
        // 检查是否已存在队列记录
        QueryWrapper<PatientQueue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("registration_id", registrationId)
                   .eq("queue_date", queueDate);
        PatientQueue existingQueue = patientQueueMapper.selectOne(queryWrapper);
        if (existingQueue != null) {
            throw new BusinessException("4008", "该挂号记录已存在队列中");
        }

        // 获取下一个队列号
        Integer nextQueueNumber = patientQueueMapper.getMaxQueueNumber(queueDate) + 1;

        PatientQueue patientQueue = new PatientQueue();
        patientQueue.setPatientId(registration.getPatientId());
        patientQueue.setRegistrationId(registrationId);
        patientQueue.setQueueDate(queueDate);
        patientQueue.setQueueNumber(nextQueueNumber);
        patientQueue.setStatus("WAITING");
        patientQueue.setPriority(priority != null ? priority : 3); // 默认优先级为3
        patientQueue.setCallCount(0);

        patientQueueMapper.insert(patientQueue);
        log.info("患者队列记录创建成功，队列ID: " + patientQueue.getId() + ", 队列号: " + nextQueueNumber);
        
        return patientQueue;
    }

    @Override
    public List<PatientQueue> getPatientQueueByStatus(LocalDate queueDate, String status) {
        log.info("获取指定状态的患者队列，日期: " + queueDate + ", 状态: " + status);
        return patientQueueMapper.findByQueueDateAndStatus(queueDate, status);
    }
}