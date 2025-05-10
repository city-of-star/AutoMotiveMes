package com.autoMotiveMes.mapper.system;

import com.autoMotiveMes.entity.system.SysPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【岗位表 mapper】
 *
 * @author li.hongyu
 * @date 2025-04-02 09:55:04
 */
@Mapper
public interface SysPostMapper extends BaseMapper<SysPost> {

    // 根据岗位名查询部门id
    Long getPostIdByName(@Param("postName") String postName);
}