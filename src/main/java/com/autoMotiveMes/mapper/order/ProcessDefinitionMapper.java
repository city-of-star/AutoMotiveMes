package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.entity.order.ProcessDefinition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 实现功能【产品工序定义表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:48:46
 */
@Mapper
public interface ProcessDefinitionMapper extends BaseMapper<ProcessDefinition> {
    // 根据产品ID获取工序定义
    @Select("SELECT * FROM process_definition WHERE product_id = #{productId} ORDER BY sequence")
    List<ProcessDefinition> selectByProductId(Long productId);

    @Select("SELECT MAX(sequence) FROM process_definition WHERE product_id = #{productId}")
    Integer selectMaxSequence(@Param("productId") Long productId);
}