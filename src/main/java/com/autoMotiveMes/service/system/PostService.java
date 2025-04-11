package com.autoMotiveMes.service.system;

import com.autoMotiveMes.dto.system.GetPostListResponseDto;
import com.autoMotiveMes.entity.system.SysPost;

import java.util.List;

/**
 * 实现功能【岗位管理服务接口】
 *
 * @author hu.hongdou
 * @date 2025-04-11 10:11:30
 */
public interface PostService {

    // 获取岗位列表
    GetPostListResponseDto getPostList();
}