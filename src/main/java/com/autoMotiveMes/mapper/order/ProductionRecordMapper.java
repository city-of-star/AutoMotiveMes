package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.dto.order.ProductionRecordQueryDto;
import com.autoMotiveMes.dto.order.ProductionRecordVo;
import com.autoMotiveMes.dto.order.ProductionStatisticsVo;
import com.autoMotiveMes.entity.order.ProductionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 实现功能【生产执行记录表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:50:58
 */
@Mapper
public interface ProductionRecordMapper extends BaseMapper<ProductionRecord> {

    // 查询生产记录
    Page<ProductionRecordVo> listProductionRecord(Page<ProductionRecordVo> page, @Param("dto") ProductionRecordQueryDto dto);

    List<ProductionRecord> selectUncheckedRecords(Page<ProductionRecord> page);

    @Select("SELECT " +
            " SUM(output_quantity) AS totalOutput, " +
            " SUM(output_quantity - defective_quantity) AS qualified " +
            "FROM production_record " +
            "WHERE DATE(start_time) = CURDATE()")
    ProductionStatisticsVo selectTodayStatistics();
}