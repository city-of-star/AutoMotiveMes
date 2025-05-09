package com.autoMotiveMes.service.business;

import com.autoMotiveMes.dto.order.SchedulePlanVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 实现功能【排程服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-28 13:40:34
 */
public interface SchedulingService {

    /**
     * 为工单生生成排程计划
     * @param orderId 工单id
     */
    void generateSchedule(Long orderId);

    /**
     * 分页查询指定工单的排程列表
     * @param orderId 工单id
     * @param page 当前页码
     * @param size 分页大小
     * @return 分页排程列表
     */
    Page<SchedulePlanVo> listSchedules(Long orderId, Integer page, Integer size);
}