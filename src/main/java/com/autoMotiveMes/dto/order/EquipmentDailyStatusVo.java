package com.autoMotiveMes.dto.order;

import lombok.Data;

/**
 * 实现功能【日报页设备状态详情】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:11:58
 */
@Data
public class EquipmentDailyStatusVo {
    private Integer totalEquipment;    // 总设备数
    private Integer normalCount;       // 正常数量
    private Integer maintenanceCount; // 维护中数量
    private Integer standbyCount;      // 待机数量
    private Integer scrapCount;        // 报废数量
}