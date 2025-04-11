package com.autoMotiveMes.dto.system;

import com.autoMotiveMes.entity.system.SysPost;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【获取岗位列表出参】
 *
 * @author hu.hongdou
 * @date 2025-04-11 10:55:24
 */
@Data
public class GetPostListResponseDto {
    private List<SysPost> posts;
}