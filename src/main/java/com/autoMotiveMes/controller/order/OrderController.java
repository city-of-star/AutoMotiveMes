package com.autoMotiveMes.controller.order;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.order.*;
import com.autoMotiveMes.entity.order.Product;
import com.autoMotiveMes.service.order.OrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/list")
    public R<Page<ProductionOrderListDto>> listOrders(@RequestBody ProductionOrderQueryDto dto) {
        return R.success(orderService.listOrders(dto));
    }

    @GetMapping("/detail/{orderId}")
    public R<ProductionOrderDetailDto> getDetail(@PathVariable Long orderId) {
        return R.success(orderService.getOrderDetail(orderId));
    }

    @PostMapping("/create")
    public R<?> createOrder(@RequestBody CreateProductionOrderDto dto) {
        orderService.createOrder(dto);
        return R.success();
    }

    @PostMapping("/delete/{orderId}")
    public R<?> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return R.success();
    }

    @PostMapping("/update-status/{orderId}/{status}")
    public R<?> updateStatus(@PathVariable Long orderId, @PathVariable Integer status) {
        orderService.updateOrderStatus(orderId, status);
        return R.success();
    }

    @GetMapping("/schedules/{orderId}")
    public R<Page<SchedulePlanDto>> getSchedules(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return R.success(orderService.listSchedules(orderId, page, size));
    }

    @PostMapping("/generate-schedule/{orderId}")
    public R<?> generateSchedule(@PathVariable Long orderId) {
        orderService.generateSchedule(orderId);
        return R.success();
    }

    @GetMapping("/product/list")
    public R<List<Product>> listProduct() {
        return R.success(orderService.listProduct());
    }

    @PostMapping("/listProductionRecord")
    public R<Page<ProductionRecordResponseDto>> listProductionRecord(@RequestBody ProductionRecordQueryDTO dto) {
        return R.success(orderService.listProductionRecord(dto));
    }

    @PostMapping("/listQualityTasks")
    public R<Page<QualityTaskDto>> listQualityTasks(@RequestBody QualityTaskQueryDto dto) {
        return R.success(orderService.listQualityTasks(dto));
    }

    @PostMapping("/submitQualityResult")
    public R<?> submitQualityResult(@RequestBody SubmitQualityResultDto dto) {
        orderService.submitQualityResult(dto);
        return R.success();
    }
}