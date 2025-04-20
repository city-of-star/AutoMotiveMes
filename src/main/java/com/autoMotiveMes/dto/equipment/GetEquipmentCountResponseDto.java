package com.autoMotiveMes.dto.equipment;

import lombok.Data;

/**
 * 实现功能【获取正常设备和在线设备的数量出参】
 *
 * @author li.hongyu
 * @date 2025-04-20 11:48:27
 */
@Data
public class GetEquipmentCountResponseDto {
    private Integer normalEquipmentCount;
    private Integer onlineEquipmentCount;
}