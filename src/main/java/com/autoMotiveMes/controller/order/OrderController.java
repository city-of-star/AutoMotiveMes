package com.autoMotiveMes.controller.order;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.order.ListWorkOrderRequestDto;
import com.autoMotiveMes.dto.order.ListWorkOrderResponseDto;
import com.autoMotiveMes.dto.order.WorkOrderDetailResponseDto;
import com.autoMotiveMes.service.order.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【工单 controller】
 *
 * @author li.hongyu
 * @date 2025-04-23 21:26:34
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/listWorkOrders")
    public R<Page<ListWorkOrderResponseDto>> listWorkOrders(@RequestBody ListWorkOrderRequestDto listWorkOrderRequestDto) {
        return R.success(orderService.listWorkOrders(listWorkOrderRequestDto));
    }

    @GetMapping("/getWorkOrderDetail/{orderId}")
    public R<WorkOrderDetailResponseDto> getWorkOrderDetail(@PathVariable Long orderId) {
        return R.success(orderService.getWorkOrderDetail(orderId));
    }
}