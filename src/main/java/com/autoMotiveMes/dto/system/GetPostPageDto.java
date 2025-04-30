package com.autoMotiveMes.dto.system;

import lombok.Data;

/**
 * 实现功能【分页查询岗位列表入参】
 *
 * @author li.hongyu
 * @date 2025-04-30 11:06:56
 */
@Data
public class GetPostPageDto {
    private Integer page;
    private Integer size;
}