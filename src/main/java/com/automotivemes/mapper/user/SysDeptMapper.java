package com.automotivemes.mapper.user;

import com.automotivemes.entity.user.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 实现功能【部门表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-02 09:54:26
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

}