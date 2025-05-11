package com.autoMotiveMes.service.impl.system;

import com.autoMotiveMes.common.constant.CommonConstant;
import com.autoMotiveMes.common.exception.BusinessException;
import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.dto.system.*;
import com.autoMotiveMes.common.exception.ServerException;
import com.autoMotiveMes.entity.system.*;
import com.autoMotiveMes.mapper.system.*;
import com.autoMotiveMes.service.system.DeptService;
import com.autoMotiveMes.service.system.PostService;
import com.autoMotiveMes.service.system.RoleService;
import com.autoMotiveMes.service.system.UserService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 实现功能【用户管理服务实现类】
 *
 * @author li.hongyu
 * @date 2025-04-05 17:56:37
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RedisTemplate<String, SysUser> userRedisTemplate;
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysDeptMapper deptMapper;
    private final SysPostMapper postMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final DeptService deptService;
    private final PostService postService;

    @Override
    public Page<SysUser> getUserPage(GetUserPageDto dto) {
        try {
            // 创建分页对象
            Page<SysUser> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                    dto.getSize() == null ? 10 : dto.getSize());

            return userMapper.searchUserPage(page, dto);
        } catch (Exception e) {
            throw new ServerException("查询用户列表出错 || " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(DeleteUserDto dto) {
        try {
            for (Long userId : dto.getUserIds()) {
                userRoleMapper.deleteByUserId(userId);
                userMapper.deleteById(userId);
            }
        } catch (Exception e) {
            throw new ServerException("删除用户出错 || " + e.getMessage());
        }
    }

    @Override
    public void switchUserStatus(SwitchUserStatusDto dto) {
        try {
            SysUser user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                throw new ServerException("修改失败，该用户不存在");
            } else if (user.getStatus() == 1) {
                user.setStatus(0);
            } else if (user.getStatus() == 0) {
                user.setStatus(1);
            } else {
                throw new ServerException("修改失败，非法的用户状态");
            }
            userMapper.updateById(user);

            // 清除旧缓存
            userRedisTemplate.delete("user:" + user.getUsername());
        } catch (Exception e) {
            throw new ServerException("切换用户状态出错 || " + e.getMessage());
        }
    }

    @Override
    public void addUser(AddUserDto dto) {
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        // 仅在手机号码非空时检查唯一性
        if (StringUtils.isNotBlank(dto.getPhone())) {
            if (userMapper.selectByPhone(dto.getPhone()) != null) {
                throw new BusinessException(ErrorCode.PHONE_EXISTS);
            }
        }
        // 仅在邮箱非空时检查唯一性
        if (StringUtils.isNotBlank(dto.getEmail())) {
            if (userMapper.selectByEmail(dto.getEmail()) != null) {
                throw new BusinessException(ErrorCode.EMAIL_EXISTS);
            }
        }

        try {
            // 存储用户信息
            SysUser user = new SysUser();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setRealName(dto.getRealName());
            user.setHeadImg(CommonConstant.HEAD_IMG_URL);  // 默认网图
            user.setThemeColor(CommonConstant.DEFAULT_THEME_COLOR);  // 默认主题颜色
            user.setStatus(dto.getStatus());
            user.setAccountLocked(1);  //  默认未锁定
            user.setLoginAttempts(0);  // 默认连续登陆失败次数为0
            user.setDeptId(dto.getDeptId());
            user.setPostId(dto.getPostId());
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);

            // 存储用户角色信息
            Long userId = userMapper.selectByUsername(dto.getUsername()).getUserId();  // 获取userId
            SysUserRole userRole = new SysUserRole(userId, dto.getRoleId());
            userRoleMapper.insert(userRole);  // 添加用户角色关系
        } catch (Exception e) {
            throw new ServerException("注册失败 || " +  e.getMessage());
        }
    }

    @Override
    public void updateUser(UpdateUserDto dto) {
        SysUser user = userMapper.selectById(dto.getUserId());  // 原本的用户信息
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXISTS);
        }
        // 仅在手机号码非空时检查唯一性
        if (StringUtils.isNotBlank(dto.getPhone())) {
            SysUser toUserByPhone = userMapper.selectByPhone(dto.getPhone());  // 用户改手机号码，查询是否已有该手机号码
            if (toUserByPhone != null && !toUserByPhone.getPhone().equals(user.getPhone())) {
                throw new BusinessException(ErrorCode.PHONE_EXISTS);
            }
        }
        // 仅在邮箱非空时检查唯一性
        if (StringUtils.isNotBlank(dto.getEmail())) {
            SysUser toUserByEmail = userMapper.selectByEmail(dto.getEmail());  // 用户改邮箱，查询是否已有该邮箱
            if (toUserByEmail != null && !toUserByEmail.getEmail().equals(user.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_EXISTS);
            }
        }
        try {
            Long userId = user.getUserId();  // 获取userId

            // 修改用户信息
            SysUser newUser = new SysUser();
            newUser.setUserId(userId);
            newUser.setPhone(dto.getPhone());
            newUser.setEmail(dto.getEmail());
            newUser.setRealName(dto.getRealName());
            newUser.setStatus(dto.getStatus());
            newUser.setDeptId(dto.getDeptId());
            newUser.setPostId(dto.getPostId());
            userMapper.updateById(newUser);

            // 修改用户的角色信息
            userRoleMapper.deleteByUserId(userId);  // 删除用户原本的角色
            SysUserRole userRole = new SysUserRole(userId, dto.getRoleId());
            userRoleMapper.insert(userRole);  // 添加新的用户角色关系

            // 清除旧缓存
            SysUser updatedUser = userMapper.selectById(userId);
            userRedisTemplate.delete("user:" + updatedUser.getUsername());
        } catch (Exception e) {
            throw new ServerException("修改失败 || " +  e.getMessage());
        }
    }

    @Override
    public GetUserInfoVo getUserInfo(GetUserInfoDto dto) {

        GetUserInfoVo res = new GetUserInfoVo();
        try {
            SysUser user = userMapper.selectById(dto.getUserId());
            res.setUsername(user.getUsername());
            res.setRealName(user.getRealName());
            res.setHeadImg(user.getHeadImg());
            res.setPhone(user.getPhone());
            res.setEmail(user.getEmail());
            res.setUserId(user.getUserId());
            res.setDeptId(user.getDeptId());
            res.setPostId(user.getPostId());
            res.setStatus(user.getStatus());
            SysUserRole userRole = userRoleMapper.selectByUserId(user.getUserId());
            res.setRoleId(userRole.getRoleId());
        } catch (Exception e) {
            throw new ServerException("获取用户信息失败" + e.getMessage());
        }
        return res;
    }

    @Override
    public void resetPassword(ResetPasswordDto dto) {
        SysUser user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXISTS);
        }
        try {
            user.setPassword(passwordEncoder.encode(CommonConstant.DEFAULT_PASSWORD));
            userMapper.updateById(user);
        } catch (Exception e) {
            throw new ServerException("重置用户密码失败" + e.getMessage());
        }
    }

    @Override
    public void getUserImportTemplate(HttpServletResponse response) {
        try {
            // 获取动态数据
            List<SysRole> roles = roleService.getRoleList();
            List<SysDept> depts = deptService.getDeptList();
            List<SysPost> posts = postService.getPostList();

            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("用户导入模板");

            // ==================== 样式定义 ====================
            // 标题样式（深蓝色背景+白色粗体）
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);      // 水平居中[3,7](@ref)
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中[3,7](@ref)

            // 数据行样式（白底黑字+居中+自动换行）
            CellStyle bodyStyle = workbook.createCellStyle();
            bodyStyle.setAlignment(HorizontalAlignment.CENTER);       // 水平居中[7](@ref)
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直居中[7](@ref)
            bodyStyle.setWrapText(true);                              // 自动换行[5](@ref)

            // ==================== 标题行 ====================
            String[] headers = {"用户名", "真实姓名", "性别(男/女)", "部门名称", "岗位名称",
                    "角色名称", "邮箱", "联系电话"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);  // 应用标题样式[7](@ref)
            }

            // ==================== 示例数据 ====================
            Row exampleRow = sheet.createRow(1);
            String[] exampleData = {
                    "test_user",
                    "测试用户",
                    "男",
                    depts.stream().map(SysDept::getDeptName).findFirst().orElse(""),  // 取第一个部门
                    posts.stream().map(SysPost::getPostName).findFirst().orElse(""),  // 第一个岗位
                    roles.stream().map(SysRole::getRoleName).findFirst().orElse(""),  // 第一个角色
                    "test@example.com",
                    "13800138000",
            };
            for (int i = 0; i < exampleData.length; i++) {
                Cell cell = exampleRow.createCell(i);
                cell.setCellValue(exampleData[i]);
                cell.setCellStyle(bodyStyle);  // 应用正文样式[6](@ref)
            }

            // 设置正文样式
            int startRow = 1;   // 起始行号（POI中行号从0开始，这里1代表第二行）
            int endRow = 1000;  // 结束行号（包含第1000行）
            int columnCount = exampleData.length;  // 列数（与示例数据列数一致）

            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                // 创建或获取行（如果行已存在则获取，否则新建）
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum);
                }

                // 遍历该行的所有列，设置样式
                for (int colNum = 0; colNum < columnCount; colNum++) {
                    // 创建或获取单元格（如果单元格已存在则获取，否则新建）
                    Cell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);
                    }

                    // 应用正文样式
                    cell.setCellStyle(bodyStyle);
                }
            }

            // ==================== 数据验证 ====================
            DataValidationHelper helper = sheet.getDataValidationHelper();
            // 部门下拉（从第2行开始，索引从0开始）
            addValidation(sheet, helper, depts.stream().map(SysDept::getDeptName).toArray(String[]::new), 3);
            // 岗位下拉
            addValidation(sheet, helper, posts.stream().map(SysPost::getPostName).toArray(String[]::new), 4);
            // 角色下拉
            addValidation(sheet, helper, roles.stream().map(SysRole::getRoleName).toArray(String[]::new), 5);

            // ==================== 列宽自适应 ====================
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i); // 自动调整列宽[5,6](@ref)
                // 针对中文列宽补偿（+2个汉字宽度）
                int columnWidth = sheet.getColumnWidth(i) + 2 * 256 * 2 * 2;
                sheet.setColumnWidth(i, Math.min(columnWidth, 255 * 256));  // 限制最大列宽[7](@ref)
            }

            // ==================== 响应输出 ====================
            String fileName = URLEncoder.encode("用户导入模板.xlsx", StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            throw new ServerException("用户导入模板生成失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void batchImportUser(MultipartFile file) {
        // 判断文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
        // 判断文件类型是否合法
        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.endsWith(".xlsx")) {
            throw new BusinessException(ErrorCode.FILE_FORMAT_ERROR);
        }

        List<SysUser> userList = new ArrayList<>();
        List<SysUserRole> userRoleList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        // 解析文件
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);  // 只有第一个Sheet

            // 获取表头索引
            Map<String, Integer> headerMap = new HashMap<>();
            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                headerMap.put(headerRow.getCell(i).getStringCellValue(), i);
            }

            // 从第2行开始读取数据
            for (int rowNum = 1; rowNum <= sheet.getPhysicalNumberOfRows(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null || isRowEmpty(row)) {
                    continue;  // 跳过空行
                }

                // 一行的数据
                SysUserTemp temp = new SysUserTemp();

                try {
                    // 用户名（必填）
                    String username = getCellValue(row, headerMap, "用户名", String.class);
                    if (StringUtils.isBlank(username)) {
                        errorMessages.add("第" + (rowNum + 1) + "行：用户名不能为空");
                        continue;
                    }
                    if (userMapper.selectByUsername(username) != null) {
                        errorMessages.add("第" + (rowNum + 1) + "行：用户名已存在");
                        continue;
                    }
                    temp.setUsername(username);

                    // 真实姓名（必填）
                    temp.setRealName(getCellValue(row, headerMap, "真实姓名", String.class));
                    if (StringUtils.isBlank(temp.getRealName())) {
                        errorMessages.add("第" + (rowNum + 1) + "行：真实姓名不能为空");
                        continue;
                    }

                    // 部门名称（必填，根据名称查询DeptId）
                    String deptName = getCellValue(row, headerMap, "部门名称", String.class);
                    if (StringUtils.isBlank(deptName)) {
                        errorMessages.add("第" + (rowNum + 1) + "行：部门名称不能为空");
                        continue;
                    }
                    Long deptId = deptMapper.getDeptIdByName(deptName);
                    if (deptId == null) {
                        errorMessages.add("第" + (rowNum + 1) + "行：部门[" + deptName + "]不存在");
                        continue;
                    }

                    // 岗位名称（必填，根据名称查询PostId）
                    String postName = getCellValue(row, headerMap, "岗位名称", String.class);
                    if (StringUtils.isBlank(postName)) {
                        errorMessages.add("第" + (rowNum + 1) + "行：岗位名称不能为空");
                        continue;
                    }
                    Long postId = postMapper.getPostIdByName(postName);
                    if (postId == null) {
                        errorMessages.add("第" + (rowNum + 1) + "行：岗位[" + postName + "]不存在");
                        continue;
                    }

                    // 角色名称（必填，根据名称查询RoleId）
                    String roleName = getCellValue(row, headerMap, "角色名称", String.class);
                    if (StringUtils.isBlank(roleName)) {
                        errorMessages.add("第" + (rowNum + 1) + "行：角色名称不能为空");
                        continue;
                    }
                    Integer roleId = roleMapper.getRoleIdByName(roleName);
                    if (roleId == null) {
                        errorMessages.add("第" + (rowNum + 1) + "行：角色[" + roleName + "]不存在");
                        continue;
                    }

                    // 性别（选填）
                    temp.setSex(getCellValue(row, headerMap, "性别(男/女)", String.class));

                    // 邮箱（选填）
                    String email = getCellValue(row, headerMap, "邮箱", String.class);
                    if (StringUtils.isNotBlank(email)) {
                        if (userMapper.selectByEmail(email) != null) {
                            errorMessages.add("第" + (rowNum + 1) + "行：邮箱已存在");
                            continue;
                        }
                    }
                    temp.setEmail(email);

                    // 手机号码（选填）
                    String phone = getCellValue(row, headerMap, "联系电话", String.class);
                    if (StringUtils.isNotBlank(phone)) {
                        if (userMapper.selectByPhone(phone) != null) {
                            errorMessages.add("第" + (rowNum + 1) + "行：手机号码已存在");
                            continue;
                        }
                    }
                    temp.setPhone(phone);

                    SysUser user = new SysUser();
                    user.setUsername(temp.getUsername());
                    user.setPassword(passwordEncoder.encode(CommonConstant.DEFAULT_PASSWORD));  // 默认密码
                    user.setRealName(temp.getRealName());
                    switch (temp.getSex()) {
                        case "女": user.setSex(0); break;
                        case "男": user.setSex(1); break;
                        default: break;
                    }
                    user.setDeptId(deptId);
                    user.setPostId(postId);
                    user.setRoleId(roleId);
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setCreateTime(LocalDateTime.now());
                    userList.add(user);

                } catch (ServerException e) {
                    throw new ServerException("出错: " + e.getMessage());
                }
            }

            // 批量插入前检查错误
            if (!errorMessages.isEmpty()) {
                String errorSummary = "导入失败，以下行存在错误：\n" + errorMessages;
                throw new BusinessException(ErrorCode.IMPORT_DATA_ERROR, errorSummary);
            }

            // 批量插入用户
            if (!userList.isEmpty()) {
                userMapper.insertBatch(userList);
                // 批量插入用户角色关系
                // userId是自增主键，插入后user对象会回填userId
                for (SysUser user : userList) {
                    userRoleList.add(new SysUserRole(user.getUserId(), user.getRoleId()));
                }
                userRoleMapper.insertBatch(userRoleList);
            }

        } catch (Exception e) {
            throw new ServerException("Excel文件读取失败：" + e.getMessage());
        }
    }

    @Override
    public void exportUser(HttpServletResponse response) {
        SXSSFWorkbook workbook = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            // 创建流式工作簿（内存保留1000行，其他写入临时文件）
            workbook = new SXSSFWorkbook(1000);
            SXSSFSheet sheet = workbook.createSheet("用户数据");

            // ==================== 样式定义 ====================
            // 表头样式（深蓝背景+白色粗体+居中）
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);      // 水平居中[3,7](@ref)
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中[3,7](@ref)

            // 数据行样式（白底黑字+居中+自动换行）
            CellStyle bodyStyle = workbook.createCellStyle();
            bodyStyle.setAlignment(HorizontalAlignment.CENTER);       // 水平居中[7](@ref)
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直居中[7](@ref)
            bodyStyle.setWrapText(true);                              // 自动换行[5](@ref)

            // ==================== 表头创建 ====================
            String[] headers = {"用户ID", "用户名", "真实姓名", "性别", "部门", "岗位", "角色", "状态",
                    "账户锁定状态", "登录尝试次数", "手机号", "邮箱", "创建时间", "更新时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);  // 应用表头样式[3,7](@ref)
            }

            // ==================== 数据写入 ====================
            int pageSize = 1000;
            int currentPage = 1;
            long total;
            int rowNum = 1;

            do {
                Page<SysUser> page = userMapper.getUserPage(new Page<>(currentPage, pageSize));
                List<SysUser> users = page.getRecords();
                total = page.getTotal();

                for (SysUser user : users) {
                    Row dataRow = sheet.createRow(rowNum++);
                    // 应用数据行样式到所有单元格
                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = dataRow.createCell(i);
                        cell.setCellStyle(bodyStyle);  // 统一应用正文样式[3,7](@ref)
                    }

                    // 数据填充（保持原有逻辑）
                    dataRow.getCell(0).setCellValue(user.getUserId());
                    dataRow.getCell(1).setCellValue(user.getUsername());
                    dataRow.getCell(2).setCellValue(user.getRealName());
                    dataRow.getCell(3).setCellValue(user.getSex() == 1 ? "男" : "女");
                    dataRow.getCell(4).setCellValue(user.getDeptName());
                    dataRow.getCell(5).setCellValue(user.getPostName());
                    dataRow.getCell(6).setCellValue(user.getRoleName());
                    dataRow.getCell(7).setCellValue(user.getStatus() == 1 ? "启用" : "禁用");
                    dataRow.getCell(8).setCellValue(user.getAccountLocked() == 1 ? "未锁定" : "已锁定");
                    dataRow.getCell(9).setCellValue(user.getLoginAttempts());
                    dataRow.getCell(10).setCellValue(user.getPhone());
                    dataRow.getCell(11).setCellValue(user.getEmail());
                    if (user.getCreateTime() != null) {
                        dataRow.getCell(12).setCellValue(user.getCreateTime().format(formatter));
                    }
                    if (user.getUpdateTime() != null) {
                        dataRow.getCell(13).setCellValue(user.getUpdateTime().format(formatter));
                    }
                }
                currentPage++;
            } while (currentPage <= (total + pageSize - 1) / pageSize);

            // ==================== 列宽优化 ====================
            sheet.trackAllColumnsForAutoSizing();
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 中文列宽补偿（增加2个汉字宽度）
                int columnWidth = sheet.getColumnWidth(i) + 2 * 256 * 2;
                sheet.setColumnWidth(i, Math.min(columnWidth, 255 * 256));  // 最大列宽限制[5](@ref)
            }

            // ==================== 响应输出 ====================
            String fileName = URLEncoder.encode("用户数据导出_"+System.currentTimeMillis()+".xlsx",
                    StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);
            workbook.write(response.getOutputStream());

        } catch (Exception e) {
            throw new ServerException("用户数据导出失败: " + e.getMessage());
        } finally {
            if (workbook != null) {
                workbook.dispose();  // 删除临时文件[6](@ref)
                try { workbook.close(); } catch (IOException ignored) {}
            }
        }
    }

    // 检查行是否为空
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        boolean isEmpty = true;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = cell.getStringCellValue().trim();
                if (!value.isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }

    // 通用单元格值获取方法（支持空值处理）
    private <T> T getCellValue(Row row, Map<String, Integer> headerMap, String fieldName, Class<T> clazz) {
        // 获取列索引
        Integer colIndex = headerMap.get(fieldName);
        if (colIndex == null) {
            throw new BusinessException(ErrorCode.IMPORT_TEMPLATE_ERROR,
                    "模板缺少字段：" + fieldName);
        }

        // 获取单元格并统一格式化为字符串
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        DataFormatter dataFormatter = new DataFormatter();
        String value = dataFormatter.formatCellValue(cell).trim();

        // 类型安全转换
        try {
            if (clazz == String.class) {
                return clazz.cast(value);
            } else if (clazz == Integer.class) {
                return clazz.cast(value.isEmpty() ? null : Integer.parseInt(value));
            } else {
                throw new IllegalArgumentException("不支持的类型: " + clazz.getName());
            }
        } catch (ClassCastException | NumberFormatException e) {
            throw new BusinessException(ErrorCode.ERROR_FIELD_TRANSFORMATION, "字段[" + fieldName + "]转换失败: " + e.getMessage());
        }
    }

    // 添加数据验证
    private void addValidation(Sheet sheet, DataValidationHelper helper,
                               String[] options, int colIndex) {
        // 设置数据验证范围
        CellRangeAddressList addressList = new CellRangeAddressList(2, 1000, colIndex, colIndex);

        // 创建数据验证约束
        DataValidationConstraint constraint = helper.createExplicitListConstraint(options);

        // 创建数据验证对象
        DataValidation validation = helper.createValidation(constraint, addressList);

        // 防止用户输入无效内容
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        validation.createErrorBox("非法输入", "请从下拉列表中选择有效值");

        // 应用验证
        sheet.addValidationData(validation);
    }

    // 用于映射用户上传的导入数据
    @Data
    public static class SysUserTemp {
        private String username;
        private String realName;
        private String sex;
        private String deptName;
        private String postName;
        private Integer roleId;
        private String email;
        private String phone;
        private Integer status;
        private Integer accountLocked;
    }
}