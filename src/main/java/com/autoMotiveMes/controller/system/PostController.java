package com.autoMotiveMes.controller.system;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.system.GetPostListResponseDto;
import com.autoMotiveMes.entity.system.SysPost;
import com.autoMotiveMes.service.system.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 实现功能【岗位管理服务controller】
 *
 * @author hu.hongdou
 * @date 2025-04-11 10:15:29
 */
@RestController
@RequestMapping("/api/system/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/list")
    @PreAuthorize("@rbacService.hasPermission(authentication, 'system:post:list')")
    public R<GetPostListResponseDto> getPostList() {
        return R.success(postService.getPostList());
    }
}