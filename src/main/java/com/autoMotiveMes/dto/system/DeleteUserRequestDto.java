package com.autoMotiveMes.dto.system;

import lombok.Data;

/**
 * 实现功能【删除用户入参】
 *
 * @author li.hongyu
 * @date 2025-04-09 16:00:04
 */
@Data
public class DeleteUserRequestDto {
    private Long[] userIds;
}