package com.autoMotiveMes.utils;

import com.autoMotiveMes.common.exception.ServerException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * 实现功能【Excel导出工具类】
 * 支持特性：
 * 1. 大数据量分页流式处理（自动内存控制）
 * 2. 动态列配置（支持对象反射映射/自定义转换）
 * 3. 多级样式配置（标题/正文/特殊列样式）
 * 4. 数据验证（下拉列表）
 * 5. 自动列宽调整（含中文适配）
 *
 * @author li.hongyu
 * @date 2025-05-11 14:32:01
 */
public class ExcelUtils {

//    // 默认样式配置
//    private static final int DEFAULT_ROW_ACCESS_WINDOW_SIZE = 1000;
//    private static final int DEFAULT_AUTO_COLUMN_WIDTH_OFFSET = 2 * 256 * 2;
//
//    /**
//     * Excel导出配置建造器
//     * @param <T> 数据类型
//     */
//    public static class ExcelExportConfig<T> {
//        private String fileName;
//        private String sheetName;
//        private List<ColumnConfig> columns;
//        private Supplier<Page<T>> dataSupplier;
//        private List<ValidationConfig> validations;
//        private CellStyle headerStyle;
//        private CellStyle bodyStyle;
//
//        // 列配置内部类
//        public static class ColumnConfig {
//            String header;
//            Function<T, Object> dataMapper;
//            CellStyle columnStyle;
//
//            public ColumnConfig(String header, Function<T, Object> dataMapper) {
//                this.header = header;
//                this.dataMapper = dataMapper;
//            }
//        }
//
//        // 数据验证配置
//        public static class ValidationConfig {
//            int colIndex;
//            String[] options;
//        }
//    }
//
//    /**
//     * 执行Excel导出
//     * @param config 导出配置
//     * @param response HTTP响应对象
//     */
//    public static <T> void export(ExcelExportConfig<T> config, HttpServletResponse response) {
//        SXSSFWorkbook workbook = null;
//        try {
//            // 1. 初始化工作簿
//            workbook = new SXSSFWorkbook(DEFAULT_ROW_ACCESS_WINDOW_SIZE);
//            Sheet sheet = workbook.createSheet(config.sheetName);
//
//            // 2. 创建样式
//            CellStyle headerStyle = config.headerStyle != null ? config.headerStyle : createDefaultHeaderStyle(workbook);
//            CellStyle bodyStyle = config.bodyStyle != null ? config.bodyStyle : createDefaultBodyStyle(workbook);
//
//            // 3. 创建表头
//            createHeaderRow(sheet, headerStyle, config.columns);
//
//            // 4. 添加数据验证
//            addDataValidations(sheet, config.validations);
//
//            // 5. 分页写入数据
//            int rowNum = 1;
//            Page<T> page;
//            do {
//                page = config.dataSupplier.get();
//                for (T data : page.getRecords()) {
//                    Row row = sheet.createRow(rowNum++);
//                    fillDataRow(row, data, config.columns, bodyStyle);
//                }
//            } while (page.getCurrent() < page.getPages());
//
//            // 6. 优化列宽
//            optimizeColumnWidth(sheet, config.columns.size());
//
//            // 7. 输出响应
//            prepareResponse(response, config.fileName);
//            workbook.write(response.getOutputStream());
//        } catch (Exception e) {
//            throw new ServerException("Excel导出失败: " + e.getMessage());
//        } finally {
//            if (workbook != null) {
//                workbook.dispose();
//                try { workbook.close(); } catch (IOException ignored) {}
//            }
//        }
//    }
//
//    // region 样式创建方法
//    private static CellStyle createDefaultHeaderStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setBold(true);
//        font.setColor(IndexedColors.WHITE.getIndex());
//        style.setFont(font);
//        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        return style;
//    }
//
//    private static CellStyle createDefaultBodyStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setWrapText(true);
//        return style;
//    }
//    // endregion
//
//    // region 工具方法
//    private static <T> void createHeaderRow(Sheet sheet, CellStyle style, List<ExcelExportConfig.ColumnConfig> columns) {
//        Row headerRow = sheet.createRow(0);
//        for (int i = 0; i < columns.size(); i++) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(columns.get(i).header);
//            cell.setCellStyle(style);
//        }
//    }
//
//    private static <T> void fillDataRow(Row row, T data, List<ExcelExportConfig.ColumnConfig> columns, CellStyle defaultStyle) {
//        for (int i = 0; i < columns.size(); i++) {
//            Cell cell = row.createCell(i);
//            Object value = columns.get(i).dataMapper.apply(data);
//            setCellValue(cell, value);
//            cell.setCellStyle(defaultStyle);
//        }
//    }
//
//    private static void setCellValue(Cell cell, Object value) {
//        if (value == null) return;
//        if (value instanceof Number) {
//            cell.setCellValue(((Number) value).doubleValue());
//        } else if (value instanceof Boolean) {
//            cell.setCellValue((Boolean) value);
//        } else if (value instanceof Date) {
//            cell.setCellValue((Date) value);
//        } else {
//            cell.setCellValue(value.toString());
//        }
//    }
//
//    private static void addDataValidations(Sheet sheet, List<ExcelExportConfig.ValidationConfig> validations) {
//        DataValidationHelper helper = sheet.getDataValidationHelper();
//        validations.forEach(v -> {
//            CellRangeAddressList addressList = new CellRangeAddressList(1, 10000, v.colIndex, v.colIndex);
//            DataValidationConstraint constraint = helper.createExplicitListConstraint(v.options);
//            DataValidation validation = helper.createValidation(constraint, addressList);
//            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
//            sheet.addValidationData(validation);
//        });
//    }
//
//    private static void optimizeColumnWidth(Sheet sheet, int columnCount) {
//        for (int i = 0; i < columnCount; i++) {
//            sheet.autoSizeColumn(i);
//            int width = sheet.getColumnWidth(i) + DEFAULT_AUTO_COLUMN_WIDTH_OFFSET;
//            sheet.setColumnWidth(i, Math.min(width, 255 * 256));
//        }
//    }
//
//    private static void prepareResponse(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
//        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + encodedFileName);
//    }
//    // endregion
}