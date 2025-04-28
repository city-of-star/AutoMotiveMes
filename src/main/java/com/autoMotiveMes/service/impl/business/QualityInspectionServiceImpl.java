package com.autoMotiveMes.service.impl.business;

import com.autoMotiveMes.common.exception.ServerException;
import com.autoMotiveMes.dto.order.QualityTaskDto;
import com.autoMotiveMes.dto.order.QualityTaskQueryDto;
import com.autoMotiveMes.dto.order.SubmitQualityResultDto;
import com.autoMotiveMes.entity.order.*;
import com.autoMotiveMes.mapper.order.*;
import com.autoMotiveMes.service.business.QualityInspectionService;
import com.autoMotiveMes.utils.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 实现功能【质检服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-28 13:56:30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QualityInspectionServiceImpl implements QualityInspectionService {

    private final ProcessDefinitionMapper processDefinitionMapper;
    private final ProductionRecordMapper productionRecordMapper;
    private final ProductionOrderMapper productionOrderMapper;
    private final QualityInspectionItemMapper qualityInspectionItemMapper;
    private final QualityInspectionRecordMapper qualityInspectionRecordMapper;

    @Scheduled(cron = "0 0/1 * * * ?") // 每1分钟检查一次
    public void checkCompletedProcess() {
        // 获取需要处理的记录（包含分页防止大数据量）
        int pageSize = 100;
        int page = 0;

        while(true) {
            Page<ProductionRecord> pageInfo = new Page<>(page, pageSize);
            List<ProductionRecord> records = productionRecordMapper.selectUncheckedRecords(pageInfo);

            if(records.isEmpty()) break;

            records.forEach(record -> {
                try {
//                    // 检查是否为末道工序
//                    if(isLastProcess(record.getProcessId())) {
//                        generateQualityTasks(record.getRecordId());
//                    }

                    // 更新标记
                    record.setQualityCheckGenerated(1);
                    productionRecordMapper.updateById(record);
                } catch (Exception e) {
                    log.error("处理记录[{}]失败: {}", record.getRecordId(), e.getMessage());
                }
            });

            page++;
        }
    }

    @Override
    public Page<QualityTaskDto> listQualityTasks(QualityTaskQueryDto dto) {
        Page<QualityTaskDto> page = new Page<>(dto.getPage(), dto.getSize());
        return qualityInspectionRecordMapper.selectQualityTasks(page, dto);
    }

    @Override
    public void generateQualityTasks(Long recordId) {
        ProductionRecord record = productionRecordMapper.selectById(recordId);
        if(record == null || record.getQualityCheckGenerated() == 1) return;

        ProcessDefinition process = processDefinitionMapper.selectById(record.getProcessId());
        if(process == null || StringUtils.isBlank(process.getQualityCheckpoints())) return;

        try {
            List<String> checkpoints = new ObjectMapper()
                    .readValue(process.getQualityCheckpoints(), new TypeReference<>(){});

            checkpoints.forEach(itemName -> {
                QualityInspectionItem item = qualityInspectionItemMapper.selectOne(
                        new QueryWrapper<QualityInspectionItem>()
                                .eq("inspection_name", itemName)
                                .eq("product_id", getProductIdByRecord(recordId))
                );

                if(item != null) {
                    createInspectionTask(record.getOrderId(), item.getItemId(), recordId);
                } else {
                    log.warn("未找到匹配的质检项: productId={}, itemName={}", process.getProductId(), itemName);
                }
            });

            // 更新状态标记
            record.setQualityCheckGenerated(1);
            productionRecordMapper.updateById(record);
        } catch (JsonProcessingException e) {
            log.error("质检项解析失败：{}", process.getQualityCheckpoints(), e);
        }
    }

    @Transactional
    @Override
    public void submitQualityResult(SubmitQualityResultDto dto) {
        QualityInspectionRecord record = qualityInspectionRecordMapper.selectById(dto.getRecordId());

        // 更新质检记录
        record.setInspectionResult(dto.getInspectionResult());
        record.setInspectionData(dto.getInspectionData());
        record.setRemark(dto.getRemark());
        record.setInspectionTime(LocalDateTime.now());
        qualityInspectionRecordMapper.updateById(record);

        // 检查所有质检项完成情况
        List<QualityInspectionRecord> records = qualityInspectionRecordMapper.selectList(
                new QueryWrapper<QualityInspectionRecord>()
                        .eq("order_id", record.getOrderId())
                        .isNull("inspection_result")
        );

        if(records.isEmpty()) {
            ProductionOrder order = productionOrderMapper.selectById(record.getOrderId());
            // 判断是否全部合格
            long unqualifiedCount = qualityInspectionRecordMapper.selectUnqualifiedCount(order.getOrderId());

            if (unqualifiedCount > 0) {
                order.setStatus(4); // 需返工
                // 自动生成返工工单
                createReworkOrder(order);
            } else {
                order.setStatus(5); // 已完成
            }
            productionOrderMapper.updateById(order);
        }
    }

    private void createInspectionTask(Long orderId, Long itemId, Long recordId) {
        Long count = qualityInspectionRecordMapper.selectCount(
                new QueryWrapper<QualityInspectionRecord>()
                        .eq("order_id", orderId)
                        .eq("item_id", itemId)
                        .eq("record_id", recordId)
        );
        if (count > 0) {
            log.warn("质检任务已存在，跳过创建");
            return;
        }

        QualityInspectionRecord task = new QualityInspectionRecord();
        task.setOrderId(orderId);
        task.setItemId(itemId);
        task.setRecordId(recordId);
        task.setInspectionResult(3);  // 待复检
        task.setInspectionTime(LocalDateTime.now());
        task.setInspector(CommonUtils.getCurrentUserId());
        qualityInspectionRecordMapper.insert(task);
    }

    // 创建返工工单
    private void createReworkOrder(ProductionOrder originalOrder) {
        // 校验是否存在有效不良品
        if (originalOrder.getDefectiveQuantity() == null || originalOrder.getDefectiveQuantity() <= 0) {
            log.error("工单{}没有需要返工的不良品数量", originalOrder.getOrderId());
            return;
        }

        // 防止重复创建返工工单
        Long existCount = productionOrderMapper.selectCount(new QueryWrapper<ProductionOrder>()
                .eq("rework_of", originalOrder.getOrderId()));
        if (existCount > 0) {
            log.info("工单{}已存在返工工单，跳过创建", originalOrder.getOrderId());
            return;
        }

        // 创建返工工单实体
        ProductionOrder reworkOrder = new ProductionOrder();
        try {
            // 生成返工工单号（R+原工单号）
            String reworkNo = "R" + originalOrder.getOrderNo();
            if (productionOrderMapper.selectCount(new QueryWrapper<ProductionOrder>().eq("order_no", reworkNo)) > 0) {
                reworkNo = reworkNo + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            }

            // 设置返工工单属性
            reworkOrder.setOrderNo(reworkNo);
            reworkOrder.setProductId(originalOrder.getProductId());
            reworkOrder.setOrderQuantity(originalOrder.getDefectiveQuantity());
            reworkOrder.setPriority(1);  // 最高优先级
            reworkOrder.setPlannedStartDate(LocalDate.now());
            reworkOrder.setPlannedEndDate(LocalDate.now().plusDays(3));
            reworkOrder.setProductionLine(originalOrder.getProductionLine());
            reworkOrder.setCreator(CommonUtils.getCurrentUserId());
            reworkOrder.setStatus(1);  // 待排程状态
            reworkOrder.setReworkOf(originalOrder.getOrderId());  // 关联原工单

            // 保存返工工单
            productionOrderMapper.insert(reworkOrder);
            log.info("创建返工工单成功，原工单：{}，返工工单：{}",
                    originalOrder.getOrderId(), reworkOrder.getOrderId());

        } catch (Exception e) {
            log.error("创建返工工单失败：{}", e.getMessage());
            throw new ServerException("返工工单创建失败");
        }
    }

    // 获取产品id通过产品记录id
    private Long getProductIdByRecord(Long recordId) {
        return Optional.ofNullable(productionOrderMapper.selectById(
                        productionRecordMapper.selectById(recordId).getOrderId()))
                .map(ProductionOrder::getProductId)
                .orElse(null);
    }
}