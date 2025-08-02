package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.me.joy.clinic.entity.MedicalOrder;

@Mapper
public interface MedicalOrderMapper extends BaseMapper<MedicalOrder> {
}