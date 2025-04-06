package com.autoMotiveMes.mapper.user;

import com.autoMotiveMes.entity.user.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 实现功能【部门表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-02 09:54:26
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
    @Select("SELECT dept_id, dept_name, parent_id, order_num FROM sys_dept WHERE status = 1 ORDER BY order_num ASC")
    List<SysDept> selectAllEnabledDepts();
}