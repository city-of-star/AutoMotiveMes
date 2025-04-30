package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.GetPostPageDto;
import com.autoMotiveMes.entity.system.SysPost;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 实现功能【岗位管理服务接口】
 *
 * @author li.hongyu
 * @date 2025-04-11 10:11:30
 */
public interface PostService {

    // 获取岗位列表
    List<SysPost> getPostList();

    // 分页查询岗位列表
    Page<SysPost> getPostPage(GetPostPageDto dto);
}