
-- 插入管理员用户
INSERT INTO users (username, email, password, created_at, updated_at) VALUES
('admin', 'admin@vps.com', '$2a$10$BiRbd1FXyd9fMufi6bM9Su8XYGxGY3lnRGCiPt9bM4P3HmkQDL7TS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入服务器类别数据
INSERT INTO server_categories (name, description, sort_order, is_active, create_time, update_time) VALUES
('{"cn": "VPS服务器", "en": "VPS Servers"}', '{"cn": "专用服务器，适用于大型应用", "en": "Virtual Private Servers for medium to big applications"}', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入服务器分组数据（使用新的JSON格式）
INSERT INTO server_groups (name, description, region, country, city, sort_order, is_active, category_id, create_time, update_time) VALUES
-- VPS服务器分组
('{"cn": "美国硅谷", "en": "Silicon Valley, USA"}', '{"cn": "美国硅谷数据中心", "en": "Silicon Valley Data Center"}', '{"cn": "美洲", "en": "Americas"}', '{"cn": "美国", "en": "United States"}', '{"cn": "硅谷", "en": "Silicon Valley"}', 1, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "美国洛杉矶", "en": "Los Angeles, USA"}', '{"cn": "美国洛杉矶数据中心", "en": "Los Angeles Data Center"}', '{"cn": "美洲", "en": "Americas"}', '{"cn": "美国", "en": "United States"}', '{"cn": "洛杉矶", "en": "Los Angeles"}', 2, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "中国香港", "en": "Hong Kong, China"}', '{"cn": "香港数据中心", "en": "Hong Kong Data Center"}', '{"cn": "亚洲", "en": "Asia"}', '{"cn": "中国", "en": "China"}', '{"cn": "香港", "en": "Hong Kong"}', 3, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "日本东京", "en": "Tokyo, Japan"}', '{"cn": "日本东京数据中心", "en": "Tokyo Data Center"}', '{"cn": "亚洲", "en": "Asia"}', '{"cn": "日本", "en": "Japan"}', '{"cn": "东京", "en": "Tokyo"}', 4, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "新加坡", "en": "Singapore"}', '{"cn": "新加坡数据中心", "en": "Singapore Data Center"}', '{"cn": "亚洲", "en": "Asia"}', '{"cn": "新加坡", "en": "Singapore"}', '{"cn": "新加坡", "en": "Singapore"}', 5, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "欧洲德国", "en": "Frankfurt, Germany"}', '{"cn": "德国法兰克福数据中心", "en": "Frankfurt Data Center"}', '{"cn": "欧洲", "en": "Europe"}', '{"cn": "德国", "en": "Germany"}', '{"cn": "法兰克福", "en": "Frankfurt"}', 6, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入服务器数据（按照新的字段结构：ip, port, status, cpu_cores, memory, disk_space, disk_type, network_speed, operating_system, username, password, group_id, create_time, last_update）
INSERT INTO servers (ip, port, status, cpu_cores, memory, disk_space, disk_type, network_speed, operating_system, username, password, group_id, create_time, last_update) VALUES
-- 美国硅谷服务器
('104.233.151.12', 19357, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.25', 49749, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.34', 6352, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.36', 55168, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.38', 44081, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.40', 41716, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.44', 60816, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.5', 10330, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'VM9epwJ8Odg1X0', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.6', 1935, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 美国洛杉矶服务器
('107.148.151.206', 61560, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.208', 16088, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.210', 65016, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.211', 13299, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.212', 2726, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.213', 14181, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.214', 2794, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.216', 41611, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.217', 33410, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.218', 28859, 'OFFLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 中国香港服务器
('107.148.45.168', 23478, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.175', 36204, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.179', 943, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.18', 38838, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.182', 54038, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.19', 57909, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.190', 49278, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.201', 45635, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.203', 9074, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.26', 15824, 'OFFLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 日本东京服务器
('104.233.219.22', 34763, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.27', 39865, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.28', 3095, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.30', 3741, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.31', 16551, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.32', 50189, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.33', 64388, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.34', 42208, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.35', 45636, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.36', 50189, 'OFFLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 新加坡服务器
('107.148.39.72', 12795, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'IjeFDPFjp5Hk', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.73', 20626, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.74', 59065, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.75', 8536, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.76', 19390, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.77', 11147, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.78', 58948, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.79', 62238, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.81', 39994, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.82', 31589, 'OFFLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 欧洲德国服务器
('107.149.202.18', 48463, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.20', 19251, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.21', 31429, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.23', 15577, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.24', 25961, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.25', 63881, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.26', 35441, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.27', 43756, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.28', 35473, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.29', 11174, 'OFFLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
