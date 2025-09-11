/*
 Navicat Premium Dump SQL

 Source Server         : local3306
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : parking

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 11/09/2025 23:11:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='管理员信息';

-- ----------------------------
-- Records of admin
-- ----------------------------
BEGIN;
INSERT INTO `admin` (`id`, `username`, `password`, `name`, `avatar`, `role`, `phone`, `email`, `created_at`, `updated_at`, `deleted_at`) VALUES (1, 'admin', '96e79218965eb72c92a549dd5a330112', '管理员12', '/files/download/2025/09/07/1757248548086-1756545083136-no.png', 'ADMIN', '13988997888', 'admin@xm.com', '2025-08-06 12:48:24', '2025-09-10 20:53:01', NULL);
COMMIT;

-- ----------------------------
-- Table structure for location
-- ----------------------------
DROP TABLE IF EXISTS `location`;
CREATE TABLE `location` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区域名称',
  `total` int DEFAULT '0' COMMENT '总车位数',
  `num` int DEFAULT '0' COMMENT '空闲车位',
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址信息',
  `address_component` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '地址组成信息',
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '省',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '市',
  `district` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `pricing_rules` json DEFAULT NULL COMMENT '计费规则（JSON格式）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='区域信息表';

-- ----------------------------
-- Records of location
-- ----------------------------
BEGIN;
INSERT INTO `location` (`id`, `name`, `total`, `num`, `address`, `address_component`, `province`, `city`, `district`, `longitude`, `latitude`, `pricing_rules`, `created_at`, `updated_at`, `deleted_at`) VALUES (1, 'A区', 5, 4, '上海市黄浦区南京东路街道南京西路213号人民公园', '{\"citycode\":\"021\",\"adcode\":\"310101\",\"businessAreas\":[],\"neighborhoodType\":\"\",\"neighborhood\":\"\",\"building\":\"\",\"buildingType\":\"\",\"street\":\"南京西路\",\"streetNumber\":\"213号\",\"province\":\"上海市\",\"city\":\"\",\"district\":\"黄浦区\",\"towncode\":\"310101002000\",\"township\":\"南京东路街道\"}', '上海市', '上海市', '黄浦区', 121.4730180, 31.2321520, '{\"dailyCap\": 50, \"freeMinutes\": 30, \"firstTierPrice\": 0.08, \"secondTierPrice\": 0.05, \"firstTierMinutes\": 120}', '2025-08-06 12:39:44', '2025-09-09 23:02:11', NULL);
INSERT INTO `location` (`id`, `name`, `total`, `num`, `address`, `address_component`, `province`, `city`, `district`, `longitude`, `latitude`, `pricing_rules`, `created_at`, `updated_at`, `deleted_at`) VALUES (2, 'B区', 8, 8, '上海市黄浦区南京东路街道百联世茂国际广场上海世茂广场', '{\"citycode\":\"021\",\"adcode\":\"310101\",\"businessAreas\":[{\"name\":\"七浦路\",\"id\":\"310106\",\"location\":[121.480979,31.245711]}],\"neighborhoodType\":\"\",\"neighborhood\":\"\",\"building\":\"百联世茂国际广场\",\"buildingType\":\"购物服务;商场;购物中心\",\"street\":\"贵州路\",\"streetNumber\":\"829号\",\"province\":\"上海市\",\"city\":\"\",\"district\":\"黄浦区\",\"towncode\":\"310101002000\",\"township\":\"南京东路街道\"}', '上海市', '上海市', '黄浦区', 121.4761470, 31.2347990, NULL, '2025-08-06 12:39:44', '2025-09-09 23:25:50', NULL);
INSERT INTO `location` (`id`, `name`, `total`, `num`, `address`, `address_component`, `province`, `city`, `district`, `longitude`, `latitude`, `pricing_rules`, `created_at`, `updated_at`, `deleted_at`) VALUES (4, 'C区', 5, 5, '上海市静安区静安寺街道胶州路瑞芝村', '{\"citycode\":\"021\",\"adcode\":\"310106\",\"businessAreas\":[{\"name\":\"曹家渡\",\"id\":\"310106\",\"location\":[121.435037,31.229951]},{\"name\":\"中山公园\",\"id\":\"310105\",\"location\":[121.426466,31.219276]}],\"neighborhoodType\":\"\",\"neighborhood\":\"\",\"building\":\"\",\"buildingType\":\"\",\"street\":\"胶州路\",\"streetNumber\":\"220号\",\"province\":\"上海市\",\"city\":\"\",\"district\":\"静安区\",\"towncode\":\"310106013000\",\"township\":\"静安寺街道\"}', '上海市', '上海市', '静安区', 121.4434240, 31.2277870, NULL, '2025-08-06 12:39:44', '2025-08-13 18:17:28', NULL);
INSERT INTO `location` (`id`, `name`, `total`, `num`, `address`, `address_component`, `province`, `city`, `district`, `longitude`, `latitude`, `pricing_rules`, `created_at`, `updated_at`, `deleted_at`) VALUES (12, 'D区', 0, 0, '上海市静安区南京西路街道成都北路335号上海招商局广场', '{\"citycode\":\"021\",\"adcode\":\"310106\",\"businessAreas\":[],\"neighborhoodType\":\"\",\"neighborhood\":\"\",\"building\":\"\",\"buildingType\":\"\",\"street\":\"成都北路\",\"streetNumber\":\"335号\",\"province\":\"上海市\",\"city\":\"\",\"district\":\"静安区\",\"towncode\":\"310106012000\",\"township\":\"南京西路街道\"}', '上海市', '上海市', '静安区', 121.4664370, 31.2283000, NULL, '2025-08-16 16:46:39', '2025-08-21 22:50:02', NULL);
COMMIT;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容',
  `time` datetime DEFAULT NULL COMMENT '发布时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='系统公告';

-- ----------------------------
-- Records of notice
-- ----------------------------
BEGIN;
INSERT INTO `notice` (`id`, `title`, `content`, `time`, `created_at`, `updated_at`, `deleted_at`) VALUES (1, '系统内测', '系统功能已全部开发完成，开始进入测试阶段。', '2025-08-16 17:17:50', '2025-08-06 12:48:59', '2025-08-16 17:17:50', NULL);
INSERT INTO `notice` (`id`, `title`, `content`, `time`, `created_at`, `updated_at`, `deleted_at`) VALUES (2, '系统上线', '所有功能都已完成，可以正常上线，发布版本v1.0。', '2025-09-11 22:23:17', '2025-08-06 12:48:59', '2025-09-11 22:23:17', NULL);
COMMIT;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '通知详细描述',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读(0-未读,1-已读)',
  `send_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户通知表';

-- ----------------------------
-- Records of notification
-- ----------------------------
BEGIN;
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (2, 2, '您的车辆在A区3号车位已超时45分钟，系统将按规定收取超时费用。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (3, 1, '温馨提醒：您的停车时长即将超时，请提前做好离场准备。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (4, 3, '您的停车订单已超时1小时，当前费用已累计至35.00元。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (5, 2, '紧急通知：您的车辆已超时停放2小时，请立即联系停车场管理处。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (6, 1, '您的停车时长已超出预约时间30分钟，可能影响后续用户使用。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (7, 3, '超时提醒：您的车辆在B区5号车位已超时15分钟。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (8, 2, '系统检测到您的停车时长已超时，请尽快处理以免产生额外费用。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (9, 1, '您的停车订单已超时，请在30分钟内完成缴费离场。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (10, 3, '重要提醒：您的车辆已超时停放，超时费用将按每分钟0.5元累计。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (11, 2, '您的停车时长已超出免费时段，请及时支付停车费用。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (12, 1, '超时警告：您的车辆已超时1小时30分钟，可能影响您的信用评级。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (13, 3, '您的停车订单已超时，当前费用为40.00元，请尽快离场。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (14, 2, '紧急通知：您的车辆已超时停放3小时，停车场将按规定采取措施。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (15, 1, '温馨提示：您的停车时长即将超时，如需继续使用请办理续停手续。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (16, 3, '您的停车订单已超时45分钟，超时费用为15.00元。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (17, 2, '系统检测到您的车辆已超时停放，请尽快联系管理员处理。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (18, 1, '超时提醒：您的停车时长已超出预约时间1小时。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (19, 3, '您的车辆在C区2号车位已超时30分钟，费用正在累计中。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (20, 2, '紧急通知：您的停车订单已超时2小时，停车场将保留相关权利。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (21, 1, '您的停车时长已超时，建议您在15分钟内离场以减少额外费用。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (22, 3, '超时警告：您的车辆已超时停放，可能会影响您下次预约车位。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (23, 2, '系统检测到您的停车订单已超时，请尽快支付费用并离场。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (24, 1, '您的停车时长已超出免费时段30分钟，当前费用为10.00元。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (25, 3, '重要提醒：您的车辆已超时停放，超时费用每分钟0.5元。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (26, 2, '您的停车订单已超时，请在1小时内完成缴费，否则将影响信用。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (27, 1, '超时警告：您的车辆已超时2小时，停车场有权采取进一步措施。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (28, 3, '您的停车时长已超时，如需继续使用请立即办理续停手续。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (29, 2, '紧急通知：您的车辆已超时停放2小时30分钟，请速联系管理员。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (30, 1, '温馨提示：您的停车时长即将超时，续停可享受优惠价格。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (31, 3, '您的停车订单已超时1小时，当前费用为30.00元。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (32, 2, '系统检测到您的车辆已超时停放，请尽快处理以避免不便。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (33, 1, '超时提醒：您的停车时长已超出预约时间45分钟。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (34, 3, '您的车辆在D区8号车位已超时30分钟，费用累计中。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (35, 2, '紧急通知：您的停车订单已超时2小时，可能影响您的会员权益。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (36, 1, '您的停车时长已超时，建议尽快离场以减少额外费用。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (37, 3, '超时警告：您的车辆已超时停放，超时费用每分钟0.6元。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (38, 2, '系统检测到您的停车订单已超时，请在30分钟内完成缴费。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (39, 1, '您的停车时长已超出免费时段1小时，当前费用为15.00元。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (40, 3, '重要提醒：您的车辆已超时停放，超时费用将按标准收取。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (41, 2, '您的停车订单已超时，请尽快处理以免影响您的下次使用。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (42, 1, '超时警告：您的车辆已超时2小时30分钟，停车场将联系您。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (43, 3, '您的停车时长已超时，如需续停请点击APP中的续停按钮。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (44, 2, '紧急通知：您的车辆已超时停放3小时，请立即处理。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (45, 1, '温馨提示：您的停车时长即将超时，续停可享受8折优惠。', 1, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-09-10 18:00:25', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (46, 3, '您的停车订单已超时1小时30分钟，当前费用为45.00元。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (47, 2, '系统检测到您的车辆已超时停放，请尽快联系管理员解决。', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (48, 2, '快点出去了', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:10', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (52, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：皖A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车65天20小时5分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:47', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (53, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：皖A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车111天22小时30分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:49', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (54, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：皖A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车111天22小时30分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:52', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (55, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：皖A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车111天22小时33分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:55', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (56, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：皖A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车111天22小时33分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:47:57', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (57, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：皖A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车112天23小时33分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:48:00', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (58, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：皖A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车113天23小时46分钟。\n\n如果您已完成停车，请及时处理22。\n\n祝您用车愉快！', 0, '2025-08-05 22:06:38', '2025-08-06 13:06:24', '2025-08-16 15:48:03', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (59, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：皖A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车122天39分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 0, '2025-08-05 22:06:38', '2025-08-13 22:59:36', '2025-08-16 15:48:05', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (60, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车124天7分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-05 22:06:38', '2025-08-15 22:26:47', '2025-09-10 17:47:26', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (61, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车124天7分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-05 22:06:38', '2025-08-15 22:27:30', '2025-09-10 17:47:28', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (62, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车124天7分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-05 22:06:38', '2025-08-15 22:28:25', '2025-09-10 17:47:31', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (63, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车124天11分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-05 22:06:38', '2025-08-15 22:31:11', '2025-09-10 17:47:34', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (64, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车124天14分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-05 22:06:38', '2025-08-15 22:34:05', '2025-09-10 17:47:37', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (65, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车124天14分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-05 22:06:38', '2025-08-15 22:34:36', '2025-09-10 20:38:00', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (66, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车124天14分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-05 22:06:38', '2025-08-15 22:34:58', '2025-09-10 20:37:55', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (67, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在B区区域A003车位。自2025-04-13 22:19:38入场以来，已停车124天14分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-05 22:06:38', '2025-08-15 22:35:43', '2025-09-10 20:37:52', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (68, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车33天21小时。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-16 19:20:25', '2025-08-16 19:20:25', '2025-09-10 21:14:22', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (69, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车34天18小时19分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-17 16:39:10', '2025-08-17 16:39:10', '2025-09-11 21:57:35', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (70, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车34天18小时19分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-17 16:39:30', '2025-08-17 16:39:29', '2025-09-11 21:57:33', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (71, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车34天18小时19分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 0, '2025-08-17 16:39:48', '2025-08-17 16:39:48', '2025-08-17 16:39:48', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (72, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车34天18小时19分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-17 16:39:58', '2025-08-17 16:39:58', '2025-09-11 21:57:20', '2025-09-11 21:57:20');
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (73, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车34天18小时19分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-17 16:40:05', '2025-08-17 16:40:05', '2025-09-11 21:56:58', '2025-09-11 21:56:58');
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (74, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车35天23小时27分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-18 21:46:46', '2025-08-18 21:46:46', '2025-09-10 21:17:58', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (75, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车35天23小时27分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-18 21:47:09', '2025-08-18 21:47:09', '2025-09-10 20:38:38', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (76, 1, '尊敬的张三车主，您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车35天23小时34分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-18 21:53:40', '2025-08-18 21:53:39', '2025-09-10 20:38:32', NULL);
INSERT INTO `notification` (`id`, `user_id`, `description`, `is_read`, `send_time`, `created_at`, `updated_at`, `deleted_at`) VALUES (77, 1, '尊敬的张三车主，零零落落您好！\n\n您的车辆（车牌号：沪A 888NB）目前停放在A区区域A003车位。自2025-07-13 22:19:38入场以来，已停车35天23小时34分钟。\n\n如果您已完成停车，请及时处理。\n\n祝您用车愉快！', 1, '2025-08-18 21:54:29', '2025-08-18 21:54:28', '2025-09-10 20:40:08', NULL);
COMMIT;

-- ----------------------------
-- Table structure for parking
-- ----------------------------
DROP TABLE IF EXISTS `parking`;
CREATE TABLE `parking` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `vehicle_id` int DEFAULT NULL COMMENT '车辆ID',
  `location_id` int DEFAULT NULL COMMENT '区域ID',
  `parking_lot_id` int DEFAULT NULL COMMENT '车位ID',
  `start_time` datetime DEFAULT NULL COMMENT '入场时间',
  `end_time` datetime DEFAULT NULL COMMENT '出场时间',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='停车信息';

-- ----------------------------
-- Records of parking
-- ----------------------------
BEGIN;
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (7, 1, 3, 1, 1, '2025-08-14 08:00:00', '2025-08-14 11:00:00', '已出场', '2025-08-06 12:49:54', '2025-08-16 16:03:25', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (8, 2, 2, 2, 10, '2025-08-14 12:00:00', '2025-08-14 15:57:06', '已出场', '2025-08-06 12:49:54', '2025-08-16 16:03:13', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (9, 1, 3, 1, 2, '2025-08-14 00:00:00', '2025-08-14 07:00:00', '已出场', '2025-08-06 12:49:54', '2025-08-16 16:03:36', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (10, 1, 1, 1, 3, '2025-08-14 15:59:14', '2025-08-14 15:59:31', '已出场', '2025-08-06 12:49:54', '2025-08-16 16:03:44', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (11, 1, 1, 1, 6, '2025-08-14 16:04:58', '2025-08-14 21:51:05', '已出场', '2025-08-06 12:49:54', '2025-08-16 16:03:52', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (12, 1, 2, 1, 3, '2025-07-13 22:19:38', NULL, '已入场', '2025-08-06 12:49:54', '2025-08-16 18:08:36', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (13, 2, 2, 2, 9, '2025-08-14 00:00:00', '2025-08-16 00:00:00', '已出场', '2025-08-16 19:32:33', '2025-08-16 19:32:54', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (14, 1, 3, 2, 10, '2025-09-01 00:00:00', '2025-09-06 00:00:00', '已出场', '2025-09-03 21:01:09', '2025-09-03 21:01:28', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (15, 1, 3, 1, 2, '2025-09-01 00:00:00', '2025-09-05 00:00:00', '已出场', '2025-09-03 21:08:38', '2025-09-03 21:08:43', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (16, 1, 3, 1, 6, '2025-09-01 00:00:00', '2025-09-10 00:00:00', '已出场', '2025-09-03 21:10:08', '2025-09-03 21:10:21', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (17, 1, 3, 1, 2, '2025-09-01 00:00:00', '2025-09-01 02:40:01', '已出场', '2025-09-03 22:06:09', '2025-09-03 22:07:26', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (18, 1, 3, 2, 9, '2025-09-03 00:00:00', '2025-09-03 23:10:00', '已出场', '2025-09-03 23:09:46', '2025-09-03 23:10:21', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (19, 1, 1, 1, 6, '2025-09-01 00:00:00', '2025-09-12 00:00:00', '已出场', '2025-09-09 23:01:58', '2025-09-09 23:02:11', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (20, 1, 1, 2, 9, '2025-09-01 00:00:00', '2025-09-09 00:00:00', '已出场', '2025-09-09 23:14:40', '2025-09-09 23:14:50', NULL);
INSERT INTO `parking` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (21, 1, 3, 2, 9, '2025-09-01 00:00:00', '2025-09-09 00:00:00', '已出场', '2025-09-09 23:25:41', '2025-09-09 23:25:50', NULL);
COMMIT;

-- ----------------------------
-- Table structure for parking_lot
-- ----------------------------
DROP TABLE IF EXISTS `parking_lot`;
CREATE TABLE `parking_lot` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '车位编号',
  `location_id` int DEFAULT NULL COMMENT '区域ID',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '空闲' COMMENT '车位状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='车位信息表';

-- ----------------------------
-- Records of parking_lot
-- ----------------------------
BEGIN;
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (1, 'A001', 1, '空闲', '2025-08-06 12:50:01', '2025-08-15 21:53:12', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (2, 'A002', 1, '空闲', '2025-08-06 12:50:01', '2025-09-03 22:07:26', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (3, 'A003', 1, '占用', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (6, 'A004', 1, '空闲', '2025-08-06 12:50:01', '2025-09-09 23:02:11', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (7, 'A005', 1, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (8, 'B001', 2, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (9, 'B002', 2, '空闲', '2025-08-06 12:50:01', '2025-09-09 23:25:50', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (10, 'B003', 2, '空闲', '2025-08-06 12:50:01', '2025-09-03 21:01:28', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (11, 'B004', 2, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (12, 'B005', 2, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (13, 'B006', 2, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (14, 'B007', 2, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (15, 'B008', 2, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (16, 'C001', 4, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (17, 'C002', 4, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (18, 'C003', 4, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (19, 'C004', 4, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
INSERT INTO `parking_lot` (`id`, `name`, `location_id`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (20, 'C005', 4, '空闲', '2025-08-06 12:50:01', '2025-08-06 12:50:01', NULL);
COMMIT;

-- ----------------------------
-- Table structure for pay
-- ----------------------------
DROP TABLE IF EXISTS `pay`;
CREATE TABLE `pay` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `vehicle_id` int DEFAULT NULL COMMENT '车辆ID',
  `location_id` int DEFAULT NULL COMMENT '区域ID',
  `parking_lot_id` int DEFAULT NULL COMMENT '车位ID',
  `start_time` datetime DEFAULT NULL COMMENT '入场时间',
  `end_time` datetime DEFAULT NULL COMMENT '出场时间',
  `minutes` int DEFAULT NULL COMMENT '分钟',
  `price` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `serial_number` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付编号',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缴费状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='停车信息';

-- ----------------------------
-- Records of pay
-- ----------------------------
BEGIN;
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (7, 1, 3, 1, 1, '2025-08-14 08:00:00', '2025-08-14 11:00:00', 180, 18.00, NULL, NULL, '已缴费', '2025-08-06 12:50:10', '2025-08-16 16:04:45', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (8, 2, 2, 2, 10, '2025-08-14 12:00:00', '2025-08-14 15:57:06', 83757, 8375.70, NULL, NULL, '已缴费', '2025-08-06 12:50:10', '2025-08-16 19:32:13', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (9, 1, 3, 1, 2, '2025-08-14 00:00:00', '2025-08-14 07:00:00', 1020, 102.00, NULL, NULL, '已缴费', '2025-08-06 12:50:10', '2025-08-16 16:04:56', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (10, 1, 1, 1, 3, '2025-08-14 15:59:14', '2025-08-14 15:59:31', 0, 0.00, NULL, NULL, '已缴费', '2025-08-06 12:50:10', '2025-08-16 16:05:03', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (11, 1, 1, 1, 6, '2025-08-14 16:04:58', '2025-08-14 21:51:05', 346, 34.60, NULL, NULL, '已缴费', '2025-08-06 12:50:10', '2025-09-09 23:01:46', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (13, 2, 2, 2, 9, '2025-08-14 00:00:00', '2025-08-16 00:00:00', 2880, 288.00, NULL, NULL, '未缴费', '2025-08-16 19:32:54', '2025-08-16 19:32:54', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (14, 1, 3, 2, 10, '2025-09-01 00:00:00', '2025-09-06 00:00:00', 7200, 720.00, NULL, NULL, '已缴费', '2025-09-03 21:01:28', '2025-09-03 21:08:16', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (15, 1, 3, 1, 2, '2025-09-01 00:00:00', '2025-09-05 00:00:00', 5760, 576.00, NULL, NULL, '已缴费', '2025-09-03 21:08:43', '2025-09-03 21:09:49', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (16, 1, 3, 1, 6, '2025-09-01 00:00:00', '2025-09-10 00:00:00', 12960, 450.00, NULL, NULL, '已缴费', '2025-09-03 21:10:21', '2025-09-03 22:04:56', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (17, 1, 3, 1, 2, '2025-09-01 00:00:00', '2025-09-01 02:40:01', 160, 10.10, NULL, NULL, '已缴费', '2025-09-03 22:07:26', '2025-09-03 23:09:44', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (18, 1, 3, 2, 9, '2025-09-03 00:00:00', '2025-09-03 23:10:00', 1390, 50.00, NULL, NULL, '已缴费', '2025-09-03 23:10:21', '2025-09-07 21:00:23', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (19, 1, 1, 1, 6, '2025-09-01 00:00:00', '2025-09-12 00:00:00', 15840, 550.00, '2025090923021140395', NULL, '已缴费', '2025-09-09 23:02:11', '2025-09-09 23:14:34', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (20, 1, 1, 2, 9, '2025-09-01 00:00:00', '2025-09-09 00:00:00', 11520, 400.00, '2025090923145037577', '2025-09-09 23:15:43', '已缴费', '2025-09-09 23:14:50', '2025-09-09 23:15:43', NULL);
INSERT INTO `pay` (`id`, `user_id`, `vehicle_id`, `location_id`, `parking_lot_id`, `start_time`, `end_time`, `minutes`, `price`, `serial_number`, `pay_time`, `status`, `created_at`, `updated_at`, `deleted_at`) VALUES (21, 1, 3, 2, 9, '2025-09-01 00:00:00', '2025-09-09 20:10:45', 11520, 400.00, '2025090923255091827', NULL, '未缴费', '2025-09-09 23:25:50', '2025-09-10 13:35:03', NULL);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `account` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户信息';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `username`, `password`, `name`, `avatar`, `role`, `phone`, `email`, `account`, `created_at`, `updated_at`, `deleted_at`) VALUES (1, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三92', 'http://localhost:9090/files/download/2025/09/11/1757601351828-ok.png', 'USER', '17374668924', '1025411125@qq.com', 605.61, '2025-08-06 12:53:08', '2025-09-11 22:35:51', NULL);
INSERT INTO `user` (`id`, `username`, `password`, `name`, `avatar`, `role`, `phone`, `email`, `account`, `created_at`, `updated_at`, `deleted_at`) VALUES (2, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'http://localhost:9090/files/download/1744530892197-14.jpg', 'USER', '18899997777', '3174120025@qq.com', 100.00, '2025-08-06 12:53:08', '2025-08-15 21:59:17', NULL);
INSERT INTO `user` (`id`, `username`, `password`, `name`, `avatar`, `role`, `phone`, `email`, `account`, `created_at`, `updated_at`, `deleted_at`) VALUES (3, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'http://localhost:9090/files/download/1744533251247-3.jpg', 'USER', NULL, NULL, 0.00, '2025-08-06 12:53:08', '2025-08-15 21:59:08', NULL);
INSERT INTO `user` (`id`, `username`, `password`, `name`, `avatar`, `role`, `phone`, `email`, `account`, `created_at`, `updated_at`, `deleted_at`) VALUES (8, '1025411125@qq.com', '96e79218965eb72c92a549dd5a330112', '1025411125@qq.com', NULL, 'USER', NULL, NULL, 0.00, '2025-08-17 16:47:49', '2025-08-17 16:47:49', NULL);
COMMIT;

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '车牌号',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='车辆信息表';

-- ----------------------------
-- Records of vehicle
-- ----------------------------
BEGIN;
INSERT INTO `vehicle` (`id`, `name`, `user_id`, `created_at`, `updated_at`, `deleted_at`) VALUES (1, '沪A N999B', 1, '2025-08-06 12:50:40', '2025-08-15 21:39:43', NULL);
INSERT INTO `vehicle` (`id`, `name`, `user_id`, `created_at`, `updated_at`, `deleted_at`) VALUES (2, '沪A 888NB', 2, '2025-08-06 12:50:40', '2025-08-15 21:39:45', NULL);
INSERT INTO `vehicle` (`id`, `name`, `user_id`, `created_at`, `updated_at`, `deleted_at`) VALUES (3, '沪A 666NB', 1, '2025-08-06 12:50:40', '2025-08-15 21:39:48', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
