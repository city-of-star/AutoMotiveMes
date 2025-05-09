package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.dto.order.DailyOrderProgressVo;
import com.autoMotiveMes.dto.order.DailyProductionDetailVo;
import com.autoMotiveMes.dto.order.DailyProductionSummaryVo;
import com.autoMotiveMes.dto.order.EquipmentDailyStatusVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 实现功能【】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:15:13
 */
@Mapper
public interface ProductionDailyReportMapper {

    DailyProductionSummaryVo selectDailySummary(@Param("date") LocalDate date);

    Page<DailyProductionDetailVo> selectDailyDetails(Page<DailyProductionDetailVo> page, @Param("date") LocalDate date);

    EquipmentDailyStatusVo selectEquipmentStatusStats();

    List<DailyOrderProgressVo> selectOrderProgress(@Param("date") LocalDate date);
}