package com.autoMotiveMes.service.impl.business;

import com.autoMotiveMes.dto.order.ProductionRecordQueryDTO;
import com.autoMotiveMes.dto.order.ProductionRecordResponseDto;
import com.autoMotiveMes.mapper.order.ProductionRecordMapper;
import com.autoMotiveMes.service.business.ProductionRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 实现功能【生产记录服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-28 14:05:06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionRecordServiceImpl implements ProductionRecordService {

    private final ProductionRecordMapper productionRecordMapper;

    @Override
    public Page<ProductionRecordResponseDto> listProductionRecord(ProductionRecordQueryDTO dto) {
        Page<ProductionRecordResponseDto> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize());
        return productionRecordMapper.listProductionRecord(page, dto);
    }
}