package com.autoMotiveMes.service.order;


import com.autoMotiveMes.dto.order.*;
import com.autoMotiveMes.entity.order.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 实现功能【工单服务】
 *
 * @author li.hongyu
 * @date 2025-04-23 21:19:17
 */
public interface OrderService {

    // 查询工单列表
    Page<ProductionOrderListDto> listOrders(ProductionOrderQueryDto dto);

    // 获取工单详情
    ProductionOrderDetailDto getOrderDetail(Long orderId);

    // 创建工单
    void createOrder(CreateProductionOrderDto dto);

    // 删除工单
    void deleteOrder(Long orderId);

    // 更新工单状态
    void updateOrderStatus(Long orderId, Integer status);

    // 根据 工单id 获取排程列表
    Page<SchedulePlanDto> listSchedules(Long orderId, Integer page, Integer size);

    // 根据 工单id 获取排程详情
    void generateSchedule(Long orderId);

    // 获取产品列表
    List<Product> listProduct();

    // 查询生产记录
    Page<ProductionRecordResponseDto> listProductionRecord(ProductionRecordQueryDTO dto);

    //
    Page<QualityTaskDto> listQualityTasks(QualityTaskQueryDto dto);

    //
    void submitQualityResult(SubmitQualityResultDto dto);

    //
    void generateQualityTasks(Long recordId);
}