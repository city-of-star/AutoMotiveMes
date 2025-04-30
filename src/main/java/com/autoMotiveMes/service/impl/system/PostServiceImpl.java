package com.autoMotiveMes.service.impl.system;

import com.autoMotiveMes.dto.system.GetPostPageDto;
import com.autoMotiveMes.entity.system.SysPost;
import com.autoMotiveMes.mapper.system.SysPostMapper;
import com.autoMotiveMes.service.system.PostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<SysPost> getPostList() {
        return postMapper.selectList(null);
    }

    @Override
    public Page<SysPost> getPostPage(GetPostPageDto dto) {
        Page<SysPost> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize());


        return null;
    }
}