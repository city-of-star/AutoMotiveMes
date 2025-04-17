package com.autoMotiveMes.controller.equipment;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.service.equipment.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实现功能【设备服务 controller】
 *
 * @author li.hongyu
 * @date 2025-04-16 16:40:20
 */
@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping("/acceptData")
    public void acceptEquipmentRealTimeData(@RequestBody EquipmentParameters data) {
        equipmentService.acceptEquipmentRealTimeData(data);
    }

    @GetMapping("/list")
    public R<List<Equipment>> listEquipment() {
        return R.success(equipmentService.listEquipment());
    }
}