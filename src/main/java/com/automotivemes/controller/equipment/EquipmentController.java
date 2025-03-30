package com.automotivemes.controller.equipment;

import com.automotivemes.common.response.R;
import com.automotivemes.entity.equipment.Equipment;
import com.automotivemes.service.equipment.EquipmentService;
import com.automotivemes.service.impl.equipment.EquipmentDataStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/equipment")
public class EquipmentController {
    private final EquipmentService equipmentService;

    private final EquipmentDataStorageService storageService;

    @PreAuthorize("@rbacService.hasPermission(authentication, 'equipment:monitor:add')")
    @PostMapping("/monitor/add")
    public R addEquipment(@RequestBody Equipment equipment) {
        equipmentService.addEquipment(equipment);
        return R.successWithoutData();
    }

    @PreAuthorize("@rbacService.hasPermission(authentication, 'equipment:monitor:delete')")
    @DeleteMapping("/monitor/delete")
    public R deleteEquipmentById(@RequestParam Integer id) {
        equipmentService.deleteEquipmentById(id);
        return R.successWithoutData();
    }

    @PreAuthorize("@rbacService.hasPermission(authentication, 'equipment:monitor:update-status')")
    @PutMapping("/monitor/update-status")
    public R updateEquipmentStatus(@RequestParam Integer id, @RequestParam String status) {
        equipmentService.updateEquipmentStatus(id, status);
        return R.successWithoutData();
    }

    @PreAuthorize("@rbacService.hasPermission(authentication, 'equipment:monitor:list')")
    @GetMapping("/monitor/list")
    public R<List<Equipment>> getAllEquipment() {
        return R.success(equipmentService.getAllEquipment());
    }

    @PostMapping("/real-time-data/simulate-create")
    public String realTimeDataSimulate(@RequestBody Map<String, String> data) {
        // 添加时间戳
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));

//        System.out.println("设备: " + data.get("equipmentId") + " 的实时信息为: "
//                + "{ "
//                + "状态: " + data.get("status")
//                + " | 温度: "
//                + data.get("temperature")
//                + " | 转速: " + data.get("rpm")
//                + " | 时间戳: " + data.get("timestamp")
//                + " }");


        storageService.realTimeDataSimulate(data);
        return "数据接收成功";
    }
}
