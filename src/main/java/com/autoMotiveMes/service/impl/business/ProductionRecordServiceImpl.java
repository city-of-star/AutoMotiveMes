package com.autoMotiveMes.service.impl.business;

import com.autoMotiveMes.dto.order.ProductionRecordQueryDto;
import com.autoMotiveMes.dto.order.ProductionRecordVo;
import com.autoMotiveMes.dto.order.ProductionStatisticsVo;
import com.autoMotiveMes.mapper.order.ProductionRecordMapper;
import com.autoMotiveMes.service.business.ProductionRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    public Page<ProductionRecordVo> listProductionRecord(ProductionRecordQueryDto dto) {
        Page<ProductionRecordVo> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize());
        return productionRecordMapper.listProductionRecord(page, dto);
    }

    @Override
    public ProductionStatisticsVo getProductionStatistics() {
        ProductionStatisticsVo stats = productionRecordMapper.selectTodayStatistics();

        // 处理无数据情况
        if (stats == null || stats.getTotalOutput() == null) {
            stats = new ProductionStatisticsVo();
            stats.setTotalOutput(0);
            stats.setQualified(0);
            stats.setYieldRate(BigDecimal.ZERO);
            return stats;
        }

        // 计算良品率
        if (stats.getTotalOutput() > 0) {
            BigDecimal yield = new BigDecimal(stats.getQualified())
                    .divide(new BigDecimal(stats.getTotalOutput()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            stats.setYieldRate(yield.setScale(2, RoundingMode.HALF_UP));
        } else {
            stats.setYieldRate(BigDecimal.ZERO);
        }

        return stats;
    }
}