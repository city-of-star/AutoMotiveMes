package com.autoMotiveMes.service.business;

import com.autoMotiveMes.dto.order.ProductionRecordQueryDTO;
import com.autoMotiveMes.dto.order.ProductionRecordResponseDto;
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
    Page<ProductionRecordResponseDto> listProductionRecord(ProductionRecordQueryDTO dto);

}