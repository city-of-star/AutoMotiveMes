# -- 删除表（先子表后主表）
# DROP TABLE IF EXISTS sys_user_role;
# DROP TABLE IF EXISTS sys_role_permission;
# DROP TABLE IF EXISTS sys_user;
# DROP TABLE IF EXISTS sys_role;
# DROP TABLE IF EXISTS sys_permission;
# DROP TABLE IF EXISTS sys_dept;
# DROP TABLE IF EXISTS sys_post;

-- 用户表
CREATE TABLE sys_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '加密密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    dept_id BIGINT COMMENT '部门id',
    post_id BIGINT COMMENT '岗位id',
    head_img VARCHAR(200) COMMENT '头像URL',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    account_locked TINYINT(1) DEFAULT 1 COMMENT '账户是否锁定(0:已锁定;1:未锁定)',
    login_attempts INT DEFAULT 0 COMMENT '连续登录失败次数',
    last_login DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色表
CREATE TABLE sys_role (
    role_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 权限表（菜单权限+操作权限）
CREATE TABLE sys_permission (
    perm_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    perm_code VARCHAR(200) NOT NULL UNIQUE COMMENT '权限编码',
    perm_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    perm_type varchar(20) NOT NULL COMMENT '权限类型(MENU,BUTTON,API)',
    parent_id INT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(100) COMMENT '前端路由路径',
    component VARCHAR(100) COMMENT '前端组件',
    icon VARCHAR(50) COMMENT '菜单图标',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    api_path VARCHAR(100) COMMENT '后端接口路径',
    method VARCHAR(10) COMMENT '请求方法'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 部门表
CREATE TABLE sys_dept (
    dept_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    parent_id BIGINT COMMENT '父部门ID（支持树形层级）',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:停用 1:启用)',
    leader_id BIGINT COMMENT '负责人ID（关联用户）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_id) REFERENCES sys_dept(dept_id),
    FOREIGN KEY (leader_id) REFERENCES sys_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 岗位表
CREATE TABLE sys_post (
    post_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    post_name VARCHAR(50) NOT NULL COMMENT '岗位名称',
    post_code VARCHAR(50) NOT NULL UNIQUE COMMENT '岗位编码',
    dept_id BIGINT NOT NULL COMMENT '所属部门ID',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:停用 1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (dept_id) REFERENCES sys_dept(dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户角色关系表
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色权限关系表
CREATE TABLE sys_role_permission (
    role_id INT NOT NULL,
    perm_id INT NOT NULL,
    PRIMARY KEY (role_id, perm_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(role_id),
    FOREIGN KEY (perm_id) REFERENCES sys_permission(perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入角色数据
INSERT INTO sys_role (role_code, role_name, description) VALUES
    ('SUPER_ADMIN', '超级管理员', '拥有系统所有权限'),
    ('EQUIP_MANAGE', '设备管理员', '拥有设备监控与维护权限');

-- 插入权限数据
INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, path, component, api_path, method) VALUES
    --  系统管理
    ('system:manage', '系统管理', 'MENU', 0, '/system', NULL, '/api/system', NULL),

    -- 用户管理模块
    ('system:system:manage', '用户管理', 'MENU', 1, '/system/auth', '/Index.vue', '/api/system/auth', NULL),  -- 2
    ('system:system:add', '新增用户', 'BUTTON', 2, NULL, NULL, '/api/system/auth/add', 'POST'),
    ('system:system:delete', '删除用户', 'BUTTON', 2, NULL, NULL, '/api/system/auth/delete', 'POST'),
    ('system:system:update', '编辑用户', 'BUTTON', 2, NULL, NULL, '/api/system/auth/update', 'POST'),
    ('system:system:list', '查询用户', 'BUTTON', 2, NULL, NULL, '/api/system/auth/list', 'GET'),
    ('system:system:import', '导入用户', 'BUTTON', 2, NULL, NULL, '/api/system/auth/import', 'POST'),
    ('system:system:export', '导出用户', 'BUTTON', 2, NULL, NULL, '/api/system/auth/export', 'GET'),


    -- 角色管理模块
    ('system:role:manage', '角色管理', 'MENU', 1, '/system/role', '/Index.vue', '/api/system/role', NULL),  -- 9
    ('system:role:add', '新增角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/add', 'POST'),
    ('system:role:delete', '删除角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/delete', 'POST'),
    ('system:role:update', '编辑角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/update', 'POST'),
    ('system:role:list', '查询角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/list', 'GET'),
    ('system:role:export', '导出角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/export', 'GET'),

    -- 部门管理模块
    ('system:dept:manage', '部门管理', 'MENU', 1, '/system/dept', '/Index.vue', '/api/system/dept', NULL),  -- 15
    ('system:dept:add', '新增部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/add', 'POST'),
    ('system:dept:delete', '删除部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/delete', 'POST'),
    ('system:dept:update', '编辑部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/update', 'POST'),
    ('system:dept:list', '查询部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/list', 'GET'),
    ('system:dept:export', '导出部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/export', 'GET'),

    -- 岗位管理模块
    ('system:post:manage', '岗位管理', 'MENU', 1, '/system/post', '/Index.vue', '/api/system/post', NULL),  -- 21
    ('system:post:add', '新增岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/add', 'POST'),
    ('system:post:delete', '删除岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/delete', 'POST'),
    ('system:post:update', '编辑岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/update', 'POST'),
    ('system:post:list', '查询岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/list', 'GET'),
    ('system:post:export', '导出岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/export', 'GET'),

    -- 设备监控
    ('equipment:manage', '设备管理与维护', 'MENU', 0, '/equipment/manage', 'equip/manage', '/api/equipment', NULL),
    ('equipment:add', '注册设备', 'BUTTON', 0, NULL, NULL, '/api/equipment/monitor/add', 'POST'),
    ('equipment:delete', '移除设备', 'BUTTON', 0, NULL, NULL, '/api/equipment/monitor/delete', 'DELETE'),
    ('equipment:update', '修改设备信息', 'BUTTON', 0, NULL, NULL, '/api/equipment/monitor/update', 'PUT'),
('equipment:list', '查询设备', 'BUTTON', 0, NULL, NULL, '/api/equipment/monitor/list', 'GET');

-- 插入用户数据 测试密码统一为123456（使用BCrypt加密存储）
INSERT INTO sys_user (username, password, real_name, dept_id, post_id, email, phone, status, account_locked, login_attempts, last_login) VALUES
    ('admin', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '超级管理员', 1, 1, '2722562862@qq.com', '18255097030',  1, false, 0, '2024-03-20 09:25:00'),
    ('lqh', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '刘齐慧', 2, 3, '2825646787@qq.com', '13855605201',  1, false, 0, '2024-03-20 08:45:00');

-- 插入部门
INSERT INTO sys_dept (dept_name, parent_id, order_num, status, leader_id) VALUES
    ('总部', null, 1, 1, 1),     -- 顶级部门，负责人为admin
    ('设备管理部', 1, 2, 1, 2);   -- 子部门，负责人为lqh

-- 插入岗位
INSERT INTO sys_post (post_name, post_code, dept_id, order_num, status) VALUES
    ('超级管理员', 'SUPER_ADMIN', 1, 1, 1),    -- 总部下的岗位
    ('管理员', 'ADMIN', 1, 2, 1),    -- 总部下的岗位
    ('设备运维', 'EQUIP_OPS', 2, 3, 1);     -- 设备管理部下的岗位

-- 用户角色关系
INSERT INTO sys_user_role (user_id, role_id) VALUES
    (1, 1),   -- admin -> SUPER_ADMIN
    (2, 2);   -- equip_1 -> EQUIP_MANAGE

-- 角色权限关系
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
    -- 超级管理员
    (1, 1),(1, 2),(1, 3),(1, 4),
    (1, 5),(1, 6),(1, 7),(1, 8),
    (1, 9),(1,10),(1, 11),(1, 12),
    (1, 13),(1, 14),(1, 15),(1, 16),
    (1, 17),(1, 18),(1, 19),(1, 20),
    (1, 21),(1, 22),(1, 23),(1, 24),
    (1, 25),(1, 26),

    -- 设备管理员
    (2, 5),(2,6),(2,7),(2,8),
    (2,9),(2, 10);


