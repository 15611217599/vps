
-- 插入管理员用户
INSERT INTO users (username, email, password, created_at, updated_at) VALUES
('admin', 'admin@vps.com', '$2a$10$BiRbd1FXyd9fMufi6bM9Su8XYGxGY3lnRGCiPt9bM4P3HmkQDL7TS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 初始化管理员用户的空钱包
INSERT INTO wallets (user_id, balance, currency, created_at, updated_at) VALUES
(1, 1000.00, 'USD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入服务器类别数据
INSERT INTO server_categories (name, description, sort_order, is_active, create_time, update_time) VALUES
('{"cn": "VPS服务器", "en": "VPS Servers"}', '{"cn": "专用服务器，适用于大型应用", "en": "Virtual Private Servers for medium to big applications"}', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入服务器分组数据（使用多语言JSON格式）
INSERT INTO server_groups (name, description, region, country, city, sort_order, is_active, category_id, create_time, update_time) VALUES
-- VPS服务器分组
('{"cn": "美国硅谷", "en": "Silicon Valley, USA"}', '{"cn": "美国硅谷数据中心", "en": "Silicon Valley Data Center"}', '{"cn": "美洲", "en": "Americas"}', '{"cn": "美国", "en": "United States"}', '{"cn": "硅谷", "en": "Silicon Valley"}', 1, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "美国洛杉矶", "en": "Los Angeles, USA"}', '{"cn": "美国洛杉矶数据中心", "en": "Los Angeles Data Center"}', '{"cn": "美洲", "en": "Americas"}', '{"cn": "美国", "en": "United States"}', '{"cn": "洛杉矶", "en": "Los Angeles"}', 2, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "中国香港", "en": "Hong Kong, China"}', '{"cn": "香港数据中心", "en": "Hong Kong Data Center"}', '{"cn": "亚洲", "en": "Asia"}', '{"cn": "中国", "en": "China"}', '{"cn": "香港", "en": "Hong Kong"}', 3, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "日本东京", "en": "Tokyo, Japan"}', '{"cn": "日本东京数据中心", "en": "Tokyo Data Center"}', '{"cn": "亚洲", "en": "Asia"}', '{"cn": "日本", "en": "Japan"}', '{"cn": "东京", "en": "Tokyo"}', 4, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "新加坡", "en": "Singapore"}', '{"cn": "新加坡数据中心", "en": "Singapore Data Center"}', '{"cn": "亚洲", "en": "Asia"}', '{"cn": "新加坡", "en": "Singapore"}', '{"cn": "新加坡", "en": "Singapore"}', 5, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('{"cn": "欧洲德国", "en": "Frankfurt, Germany"}', '{"cn": "德国法兰克福数据中心", "en": "Frankfurt Data Center"}', '{"cn": "欧洲", "en": "Europe"}', '{"cn": "德国", "en": "Germany"}', '{"cn": "法兰克福", "en": "Frankfurt"}', 6, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入服务器数据（按照新的字段结构：ip, port, status, cpu_cores, memory, disk_space, disk_type, network_speed, operating_system, username, password, is_sold, group_id, create_time, last_update）
INSERT INTO servers (ip, port, status, cpu_cores, memory, disk_space, disk_type, network_speed, operating_system, username, password, is_sold, group_id, create_time, last_update) VALUES
-- 美国硅谷服务器
('104.233.151.12', 19357, 'ONLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.25', 49749, 'ONLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.34', 6352, 'ONLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.36', 55168, 'ONLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.38', 44081, 'ONLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.40', 41716, 'ONLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.44', 60816, 'ONLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.5', 10330, 'ONLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'VM9epwJ8Odg1X0', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.151.6', 1935, 'ONLINE', '32 Core', '32G', '200G SSD', 'SV - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', '7xZ5o2tyRWnPE4', false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 美国洛杉矶服务器
('107.148.151.206', 61560, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.208', 16088, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.210', 65016, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.211', 13299, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.212', 2726, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.213', 14181, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.214', 2794, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.216', 41611, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.217', 33410, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.151.218', 28859, 'ONLINE', '32 Core', '32G', '200G SSD', 'LA - SSD', '50M', 'CentOS - 7.9.2111 - x64', 'root', 'mEgIrM9Ww6x28P', false, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 中国香港服务器
('107.148.45.168', 23478, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.175', 36204, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.179', 943, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.18', 38838, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.182', 54038, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.19', 57909, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.190', 49278, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.201', 45635, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.203', 9074, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.45.26', 15824, 'ONLINE', '32 Core', '32G', '200G SSD', 'HK - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'bOd2C6AB80wVqs', false, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 日本东京服务器
('104.233.219.22', 34763, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.27', 39865, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.28', 3095, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.30', 3741, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.31', 16551, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.32', 50189, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.33', 64388, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.34', 42208, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.35', 45636, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('104.233.219.36', 50189, 'ONLINE', '32 Core', '32G', '200G SSD', 'TKY - HDD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'UA74XGkwHcbq89', false, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 新加坡服务器
('107.148.39.72', 12795, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'IjeFDPFjp5Hk', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.73', 20626, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.74', 59065, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.75', 8536, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.76', 19390, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.77', 11147, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.78', 58948, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.79', 62238, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.81', 39994, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.148.39.82', 31589, 'ONLINE', '32 Core', '32G', '200G SSD', 'SG - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'ZoLncuD7Mt1Y68', false, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- 欧洲德国服务器
('107.149.202.18', 48463, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.20', 19251, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.21', 31429, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.23', 15577, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.24', 25961, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.25', 63881, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.26', 35441, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.27', 43756, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.28', 35473, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('107.149.202.29', 11174, 'ONLINE', '32 Core', '32G', '200G SSD', 'FR - SSD', '25M', 'CentOS - 7.9.2111 - x64', 'root', 'Ld4MBRw578Shbn', false, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入价格组数据（包含多语言和示例HTML）
INSERT INTO price_groups (name, description, hourly_price, daily_price, monthly_price, quarterly_price, semi_annual_price, annual_price, sort_order, is_active, server_group_id, sales_page_html, create_time, last_update) VALUES
    ('{"cn": "标准套餐", "en": "Standard Package"}',
     '{"cn": "适合个人用户和小型项目的标准价格套餐", "en": "Standard pricing package suitable for individual users and small projects"}',
     1.00, 10.00, 200.00, 500.00, 900.00, 1500.00,
     1, true, 2,
     '{"cpu_cores":32,"memory":32,"disk_space":200,"network_speed":25,"ip_count":1,"operating_system":[{"name":"ubuntu","versions":["16.04","18.04","20.04","22.04","24.04","25.04"]},{"name":"centos","versions":["9","10"]},{"name":"debian","versions":["9","10","11","12","13"]}]}',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);