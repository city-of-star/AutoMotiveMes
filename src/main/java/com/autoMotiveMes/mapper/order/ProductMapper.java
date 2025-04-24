package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.entity.order.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 实现功能【产品基础信息表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-24 09:49:50
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}