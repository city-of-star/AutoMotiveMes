package com.autoMotiveMes.dto.user;

import lombok.Data;

/**
 * 实现功能【删除用户入参】
 *
 * @author li.hongyu
 * @date 2025-04-08 20:02:00
 */
@Data
public class DeleteUserRequestDto {
    private Long[] userIds;
}