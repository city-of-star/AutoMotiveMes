package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.dto.order.ProductionRecordQueryDTO;
import com.autoMotiveMes.dto.order.ProductionRecordResponseDto;
import com.autoMotiveMes.entity.order.ProductionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    Page<ProductionRecordResponseDto> listProductionRecord(Page<ProductionRecordResponseDto> page, @Param("dto") ProductionRecordQueryDTO dto);

    List<ProductionRecord> selectUncheckedRecords(Page<ProductionRecord> page);
}