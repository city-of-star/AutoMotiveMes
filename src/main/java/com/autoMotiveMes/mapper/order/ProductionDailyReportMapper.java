package com.autoMotiveMes.mapper.order;

import com.autoMotiveMes.dto.order.DailyOrderProgressDto;
import com.autoMotiveMes.dto.order.DailyProductionDetailDto;
import com.autoMotiveMes.dto.order.DailyProductionSummaryDto;
import com.autoMotiveMes.dto.order.EquipmentDailyStatusDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 实现功能【】
 *
 * @author li.hongyu
 * @date 2025-04-29 09:15:13
 */
@Mapper
public interface ProductionDailyReportMapper {

    @Select("SELECT " +
            "COALESCE(SUM(output_quantity), 0) AS totalOutput," +
            "COALESCE(SUM(output_quantity - defective_quantity), 0) AS qualifiedProducts," +
            "COALESCE(COUNT(CASE WHEN remark IS NOT NULL THEN 1 END), 0) AS abnormalRecords " +
            "FROM production_record " +
            "WHERE DATE(start_time) = #{date}")
    DailyProductionSummaryDto selectDailySummary(@Param("date") LocalDate date);

    @Select("SELECT " +
            "po.order_no, p.product_code, pd.process_name, " +
            "e.equipment_code, pr.output_quantity, pr.defective_quantity, " +
            "u.real_name AS operatorName, pr.start_time, pr.end_time, " +
            "TIMEDIFF(pr.end_time, pr.start_time) AS duration " +
            "FROM production_record pr " +
            "JOIN production_order po ON pr.order_id = po.order_id " +
            "JOIN product p ON po.product_id = p.product_id " +
            "JOIN process_definition pd ON pr.process_id = pd.process_id " +
            "JOIN equipment e ON pr.equipment_id = e.equipment_id " +
            "JOIN sys_user u ON pr.operator = u.user_id " +
            "WHERE DATE(pr.start_time) = #{date} " +
            "ORDER BY pr.start_time DESC")
    Page<DailyProductionDetailDto> selectDailyDetails(Page<DailyProductionDetailDto> page, @Param("date") LocalDate date);

    @Select("SELECT " +
            "COUNT(*) AS totalEquipment, " +
            "COUNT(CASE WHEN status = 1 THEN 1 END) AS normalCount, " +
            "COUNT(CASE WHEN status = 3 THEN 1 END) AS maintenanceCount, " +
            "COUNT(CASE WHEN status = 2 THEN 1 END) AS standbyCount, " +
            "COUNT(CASE WHEN status = 4 THEN 1 END) AS scrapCount " +
            "FROM equipment")
    EquipmentDailyStatusDto selectEquipmentStatusStats();

    @Select("SELECT " +
            "po.order_no, p.product_name, po.order_quantity AS totalQuantity, " +
            "po.completed_quantity AS completedQuantity, " +
            "ROUND((po.completed_quantity * 100.0 / po.order_quantity), 2) AS progressRate, " +
            "COALESCE(pd_current.process_name, pd_last.process_name) AS currentProcess " +
            "FROM production_order po " +
            "JOIN product p ON po.product_id = p.product_id " +
            "LEFT JOIN (" +
            "   SELECT ps1.* FROM production_schedule ps1 " +
            "   WHERE ps1.schedule_status = 2 " +
            "   OR (ps1.schedule_status = 3 AND ps1.planned_end_time = (" +
            "       SELECT MAX(planned_end_time) FROM production_schedule ps2 " +
            "       WHERE ps2.order_id = ps1.order_id AND ps2.schedule_status = 3" +
            "   ))" +
            ") ps ON po.order_id = ps.order_id " +
            "LEFT JOIN process_definition pd_current ON ps.process_id = pd_current.process_id " +
            "LEFT JOIN (" +
            "   SELECT ps3.order_id, MAX(ps3.planned_end_time) AS last_end_time " +
            "   FROM production_schedule ps3 WHERE ps3.schedule_status = 3 " +
            "   GROUP BY ps3.order_id" +
            ") last_ps ON po.order_id = last_ps.order_id " +
            "LEFT JOIN process_definition pd_last ON (" +
            "   SELECT ps4.process_id FROM production_schedule ps4 " +
            "   WHERE ps4.order_id = last_ps.order_id AND ps4.planned_end_time = last_ps.last_end_time" +
            ") = pd_last.process_id " +
            "WHERE (po.status BETWEEN 2 AND 5) " +
            "AND (po.planned_start_date <= #{date} AND po.planned_end_date >= #{date}) " +
            "ORDER BY po.priority")
    List<DailyOrderProgressDto> selectOrderProgress(@Param("date") LocalDate date);
}