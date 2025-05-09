package com.autoMotiveMes.controller.business;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.order.SchedulePlanVo;
import com.autoMotiveMes.service.business.SchedulingService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【排程 Controller】
 *
 * @author li.hongyu
 * @date 2025-05-05 12:20:13
 */
@RestController
@RequestMapping("/api/scheduling")
@RequiredArgsConstructor
public class SchedulingController {

    private final SchedulingService schedulingService;

    @GetMapping("/get/{orderId}")
    public R<Page<SchedulePlanVo>> getSchedules(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return R.success(schedulingService.listSchedules(orderId, page, size));
    }

    @PostMapping("/generate/{orderId}")
    public R<?> generateSchedule(@PathVariable Long orderId) {
        schedulingService.generateSchedule(orderId);
        return R.success();
    }
}