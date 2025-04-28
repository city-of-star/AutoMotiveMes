package com.autoMotiveMes.dto.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 实现功能【今日产量统计出参】
 *
 * @author li.hongyu
 * @date 2025-04-28 16:20:08
 */
@Data
public class ProductionStatisticsDto {
    private Integer totalOutput;      // 总产出
    private Integer qualified;        // 合格品数量
    private BigDecimal yieldRate;     // 良品率
}