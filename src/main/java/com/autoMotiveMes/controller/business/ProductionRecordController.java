package com.autoMotiveMes.controller.business;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.order.ProductionRecordQueryDTO;
import com.autoMotiveMes.dto.order.ProductionRecordResponseDto;
import com.autoMotiveMes.dto.order.ProductionStatisticsDto;
import com.autoMotiveMes.service.business.ProductionRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【生产记录 controller】
 *
 * @author li.hongyu
 * @date 2025-05-05 12:14:24
 */
@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
public class ProductionRecordController {

    private final ProductionRecordService productionRecordService;

    @PostMapping("/listProductionRecord")
    public R<Page<ProductionRecordResponseDto>> listProductionRecord(@RequestBody ProductionRecordQueryDTO dto) {
        return R.success(productionRecordService.listProductionRecord(dto));
    }

    @GetMapping("/statistics")
    public R<ProductionStatisticsDto> getProductionStats() {
        return R.success(productionRecordService.getProductionStatistics());
    }
}