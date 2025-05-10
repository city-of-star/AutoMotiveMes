package com.autoMotiveMes.mapper.system;

import com.autoMotiveMes.entity.system.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实现功能【部门表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-02 09:54:26
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    // 查询所有启用的部门
    List<SysDept> selectAllEnabledDepts();

    // 根据部门名查询部门id
    Long getDeptIdByName(@Param("deptName") String deptName);
}