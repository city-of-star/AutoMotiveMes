package com.autoMotiveMes.controller.business;

import com.autoMotiveMes.common.response.R;
import com.autoMotiveMes.dto.order.QualityTaskDto;
import com.autoMotiveMes.dto.order.QualityTaskQueryDto;
import com.autoMotiveMes.dto.order.SubmitQualityResultDto;
import com.autoMotiveMes.service.business.QualityInspectionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【质量检测 controller】
 *
 * @author li.hongyu
 * @date 2025-05-05 12:18:33
 */
@RestController
@RequestMapping("/api/qualityInspection")
@RequiredArgsConstructor
public class QualityInspectionController {

    private final QualityInspectionService qualityInspectionService;

    @PostMapping("/listQualityTasks")
    public R<Page<QualityTaskDto>> listQualityTasks(@RequestBody QualityTaskQueryDto dto) {
        return R.success(qualityInspectionService.listQualityTasks(dto));
    }

    @PostMapping("/submitQualityResult")
    public R<?> submitQualityResult(@RequestBody SubmitQualityResultDto dto) {
        qualityInspectionService.submitQualityResult(dto);
        return R.success();
    }

}