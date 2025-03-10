package com.automotivemes.controller.equipment;

import com.automotivemes.common.response.CommonResponse;
import com.automotivemes.common.response.ResponseUtils;
import com.automotivemes.entity.equipment.Equipment;
import com.automotivemes.service.equipment.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/equipment")
public class EquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping("/add")
    public ResponseEntity<CommonResponse<Object>> addEquipment(@RequestBody Equipment equipment) {
        equipmentService.addEquipment(equipment);
        return ResponseUtils.okWithoutData();
    }
}
