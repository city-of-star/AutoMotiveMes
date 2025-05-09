package com.autoMotiveMes.controller.business;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.order.ProductionRecordQueryDto;
import com.autoMotiveMes.dto.order.ProductionRecordVo;
import com.autoMotiveMes.dto.order.ProductionStatisticsVo;
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
    public R<Page<ProductionRecordVo>> listProductionRecord(@RequestBody ProductionRecordQueryDto dto) {
        return R.success(productionRecordService.listProductionRecord(dto));
    }

    @GetMapping("/statistics")
    public R<ProductionStatisticsVo> getProductionStats() {
        return R.success(productionRecordService.getProductionStatistics());
    }
}