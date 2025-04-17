package com.autoMotiveMes.service.impl.system;

import com.autoMotiveMes.dto.system.GetPostListResponseDto;
import com.autoMotiveMes.mapper.system.SysPostMapper;
import com.autoMotiveMes.service.system.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 实现功能【岗位管理服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-11 10:14:10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final SysPostMapper postMapper;

    @Override
    public GetPostListResponseDto getPostList() {
        GetPostListResponseDto dto = new GetPostListResponseDto();
        dto.setPosts(postMapper.selectList(null));
        return dto;
    }
}