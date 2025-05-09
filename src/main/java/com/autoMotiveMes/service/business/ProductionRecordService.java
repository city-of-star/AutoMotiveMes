package com.autoMotiveMes.service.business;

import com.autoMotiveMes.dto.order.ProductionRecordQueryDto;
import com.autoMotiveMes.dto.order.ProductionRecordVo;
import com.autoMotiveMes.dto.order.ProductionStatisticsVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 实现功能【生产记录服务】
 *
 * @author li.hongyu
 * @date 2025-04-28 14:04:06
 */
public interface ProductionRecordService {

    /**
     * 分页查询生产记录列表
     * @param dto 查询条件
     * @return 分页生产记录列表
     */
    Page<ProductionRecordVo> listProductionRecord(ProductionRecordQueryDto dto);

    /**
     * 获取今日产量统计数据
     * @return 今日产量统计数据
     */
    ProductionStatisticsVo getProductionStatistics();
}