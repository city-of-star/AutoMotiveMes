package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.dto.order.QualityTaskDto;
import com.autoMotiveMes.dto.order.QualityTaskQueryDto;
import com.autoMotiveMes.entity.order.QualityInspectionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 实现功能【质量检测记录表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:50:42
 */
@Mapper
public interface QualityInspectionRecordMapper extends BaseMapper<QualityInspectionRecord> {

    Page<QualityTaskDto> selectQualityTasks(Page<QualityTaskDto> page, @Param("dto") QualityTaskQueryDto dto);

    @Select("SELECT COUNT(*) FROM quality_inspection_record " +
            "WHERE order_id = #{orderId} AND inspection_result = 2")
    Long selectUnqualifiedCount(Long orderId);
}
