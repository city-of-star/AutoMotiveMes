-- 清理数据
DELETE FROM sys_user_role;
DELETE FROM sys_role_permission;
DELETE FROM sys_user;
DELETE FROM sys_role;
DELETE FROM sys_permission;

-- 初始化权限
INSERT INTO sys_permission (perm_id, perm_code, perm_name, perm_type, api_path, method) VALUES
    (1, 'USER_MANAGE', '用户管理', 'API', '/api/users/**', 'ALL'),
    (2, 'DEVICE_CONTROL', '设备控制', 'API', '/api/devices/*/control', 'POST');

-- 初始化角色
INSERT INTO sys_role (role_id, role_code, role_name) VALUES
    (1, 'ADMIN', '管理员'),
    (2, 'DEVICE_OPERATOR', '设备操作员');

-- 关联角色权限
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
    (1, 1), (1, 2),  -- ADMIN拥有所有权限
    (2, 2);           -- 操作员只有设备控制权限

-- 初始化用户
INSERT INTO sys_user (user_id, username, password, status) VALUES
    (1, 'admin', '$2a$10$lzHwLJi8orUSFver5XRHm.CEdM60bt5H1W.hF/YTMTJMz/1LmOjh.', 1), -- 密码admin123
    (2, 'operator', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 1);

-- 关联用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
    (1, 1),  -- admin是管理员
    (2, 2);  -- operator是操作员