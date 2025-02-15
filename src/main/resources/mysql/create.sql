-- 用户表
CREATE TABLE sys_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '加密密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
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
    perm_code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
    perm_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    perm_type ENUM('MENU','BUTTON','API') NOT NULL COMMENT '权限类型',
    parent_id INT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(100) COMMENT '前端路由路径',
    component VARCHAR(100) COMMENT '前端组件',
    icon VARCHAR(50) COMMENT '菜单图标',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    api_path VARCHAR(100) COMMENT '后端接口路径',
    method VARCHAR(10) COMMENT '请求方法'
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

-- 初始化数据
INSERT INTO sys_role (role_code, role_name, description) VALUES
    ('ADMIN', '系统管理员', '拥有所有权限'),
    ('PROD_MANAGER', '生产主管', '生产管理相关权限'),
    ('DEVICE_OPERATOR', '设备操作员', '设备操作权限');

INSERT INTO sys_permission (perm_code, perm_name, perm_type, path, component, api_path, method) VALUES
    ('USER_MANAGE', '用户管理', 'MENU', '/system/user', 'system/UserManage', '/api/users/**', 'ALL'),
    ('ROLE_MANAGE', '角色管理', 'MENU', '/system/role', 'system/RoleManage', '/api/roles/**', 'ALL'),
    ('PROD_ORDER_CREATE', '创建工单', 'BUTTON', NULL, NULL, '/api/production/orders', 'POST'),
    ('DEVICE_CONTROL', '设备控制', 'API', NULL, NULL, '/api/devices/*/control', 'POST');