package com.autoMotiveMes.controller.business;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.order.*;
import com.autoMotiveMes.entity.order.Product;
import com.autoMotiveMes.service.business.OrderService;
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
    public R<Page<ProductionOrderListVo>> listOrders(@RequestBody ProductionOrderQueryDto dto) {
        return R.success(orderService.listOrders(dto));
    }

    @GetMapping("/detail/{orderId}")
    public R<ProductionOrderDetailVo> getDetail(@PathVariable Long orderId) {
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

    @GetMapping("/product/list")
    public R<List<Product>> listProduct() {
        return R.success(orderService.listProduct());
    }
}