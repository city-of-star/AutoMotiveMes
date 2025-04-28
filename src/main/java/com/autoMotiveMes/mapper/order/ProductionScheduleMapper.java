package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.dto.order.SchedulePlanDto;
import com.autoMotiveMes.entity.order.ProductionSchedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【生产排程计划表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:51:14
 */
@Mapper
public interface ProductionScheduleMapper extends BaseMapper<ProductionSchedule> {

    // 获取排程列表
    Page<SchedulePlanDto> listSchedules(Page<SchedulePlanDto> page, @Param("orderId") Long orderId);

    int countRunningSchedules(Long equipmentId);
}
