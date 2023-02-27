-- MySQL dump 10.13  Distrib 8.0.29, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: 242wvp
-- ------------------------------------------------------
-- Server version	8.0.29-0ubuntu0.22.04.3

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `deviceId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `manufacturer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `firmware` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `transport` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `streamMode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `online` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `registerTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `keepaliveTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `createTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `updateTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `expires` int NOT NULL,
    `subscribeCycleForCatalog` int NOT NULL,
    `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `port` int DEFAULT NULL,
    `hostAddress` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `netmask` varchar(50) DEFAULT NULL COMMENT '子网掩码',
    `gateway` varchar(50) DEFAULT NULL COMMENT '网关',
    `superviseTargetType` int(11) DEFAULT NULL COMMENT '监视物类型, @supervise_target_type.type',
    `superviseTargetId` int(11) DEFAULT NULL COMMENT '监视物ID, 类型：@supervise_target.id',
    `position` varchar(255) DEFAULT NULL COMMENT '摄像头安装位置',
    `carriageNo` int DEFAULT NULL COMMENT '摄像机所在车厢号',
    `charset` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `subscribeCycleForMobilePosition` int DEFAULT NULL,
    `mobilePositionSubmissionInterval` int DEFAULT '5',
    `subscribeCycleForAlarm` int DEFAULT NULL,
    `ssrcCheck` int DEFAULT '0',
    `geoCoordSys` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `treeType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `device_deviceId_uindex` (`deviceId`),
    UNIQUE KEY `device_ip_uindex` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device_alarm`
--

DROP TABLE IF EXISTS `device_alarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_alarm` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `deviceId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `channelId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `alarmPriority` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `alarmMethod` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `alarmTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `alarmDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `longitude` double DEFAULT NULL,
    `latitude` double DEFAULT NULL,
    `alarmType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `createTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `alreadyTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `alreadyUser` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_alarmType`(`alarmType`) USING BTREE,
    INDEX `idx_alarmPriority`(`alarmPriority`) USING BTREE,
    INDEX `idx_createTime`(`createTime`) USING BTREE,
    INDEX `idx_alreadyTime`(`alreadyTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_alarm`
--

LOCK TABLES `device_alarm` WRITE;
/*!40000 ALTER TABLE `device_alarm` DISABLE KEYS */;
/*!40000 ALTER TABLE `device_alarm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device_channel`
--

DROP TABLE IF EXISTS `device_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_channel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channelId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `manufacture` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `owner` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `civilCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `block` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `parentId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `safetyWay` int DEFAULT NULL,
  `registerWay` int DEFAULT NULL,
  `certNum` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `certifiable` int DEFAULT NULL,
  `errCode` int DEFAULT NULL,
  `endTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `secrecy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `ipAddress` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `port` int DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `PTZType` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `streamId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `deviceId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `parental` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `hasAudio` bit(1) DEFAULT NULL,
  `createTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `updateTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `subCount` int DEFAULT '0',
  `longitudeGcj02` double DEFAULT NULL,
  `latitudeGcj02` double DEFAULT NULL,
  `longitudeWgs84` double DEFAULT NULL,
  `latitudeWgs84` double DEFAULT NULL,
  `businessGroupId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `gpsTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `storagePlan` bigint unsigned DEFAULT NULL COMMENT '存储计划',
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_channel_pk` (`channelId`,`deviceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_channel`
--

LOCK TABLES `device_channel` WRITE;
/*!40000 ALTER TABLE `device_channel` DISABLE KEYS */;
/*!40000 ALTER TABLE `device_channel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device_mobile_position`
--

DROP TABLE IF EXISTS `device_mobile_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_mobile_position` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deviceId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `channelId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `deviceName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  `altitude` double DEFAULT NULL,
  `speed` double DEFAULT NULL,
  `direction` double DEFAULT NULL,
  `reportSource` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `longitudeGcj02` double DEFAULT NULL,
  `latitudeGcj02` double DEFAULT NULL,
  `longitudeWgs84` double DEFAULT NULL,
  `latitudeWgs84` double DEFAULT NULL,
  `createTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_mobile_position`
--

LOCK TABLES `device_mobile_position` WRITE;
/*!40000 ALTER TABLE `device_mobile_position` DISABLE KEYS */;
/*!40000 ALTER TABLE `device_mobile_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `access_log`
--

DROP TABLE IF EXISTS `access_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access_log` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
   `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
   `uri` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
   `address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
   `result` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
   `timing` bigint NOT NULL,
   `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
   `createTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_log`
--

LOCK TABLES `access_log` WRITE;
/*!40000 ALTER TABLE `access_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `access_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
    `id` int NOT NULL AUTO_INCREMENT,
    `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `roleId` int NOT NULL,
    `createTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `updateTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `user_username_uindex` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','21232f297a57a5a743894a0e4a801fc3','css', '', 1, '2022-10-12 14:14:57','2022-10-12 14:14:57');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
     `id` int NOT NULL AUTO_INCREMENT,
     `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
     `authority` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
     `createTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
     `updateTime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1, '超级管理员', '0', '2022-10-12 14:14:57', '2022-10-12 14:14:57');
INSERT INTO `user_role` VALUES (2, '管理员', '0', '2022-10-12 14:14:57', '2022-10-12 14:14:57');
INSERT INTO `user_role` VALUES (3, '操作员', '0', '2022-10-12 14:14:57', '2022-10-12 14:14:57');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-09-08 16:46:27

--
-- Table structure for table `storage_plan`
--

DROP TABLE IF EXISTS `storage_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `storage_plan` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(60) NOT NULL COMMENT '存储计划名称',
    `enable` bigint NOT NULL COMMENT '是否启用，0-未启用，1-启用',
    `cover` bigint unsigned NOT NULL COMMENT '存储计划覆盖天数',
    `directory` varchar(255) NOT NULL COMMENT '存储目录',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储计划表';

-- 监视物类型 --
DROP TABLE IF EXISTS `supervise_target_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supervise_target_type`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `type` INTEGER NOT NULL COMMENT '类型：1：转向架，2：受电弓，3，车厢',
    `name` varchar(100) NOT NULL COMMENT '类型名称',
    `description` varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `unique_type` (`type`) USING BTREE,
    UNIQUE KEY `unique_name` (`name`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '监视物类型';

LOCK TABLES `supervise_target_type` WRITE;
/*!40000 ALTER TABLE `supervise_target_type` DISABLE KEYS */;
INSERT INTO `supervise_target_type` VALUES (1, '1', '转向架', '');
INSERT INTO `supervise_target_type` VALUES (2, '2', '受电弓', '');
INSERT INTO `supervise_target_type` VALUES (3, '3', '车厢', '');
/*!40000 ALTER TABLE `supervise_target_type` ENABLE KEYS */;
UNLOCK TABLES;

-- 监视物 --
DROP TABLE IF EXISTS `supervise_target`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supervise_target`  (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(100) NOT NULL COMMENT '监视物名称',
    `type` INTEGER NOT NULL COMMENT '类型：@supervise_target_type.type',
    `carriageNo` int(11) DEFAULT NULL COMMENT '监视物所在车厢',
    `address` varchar(255) DEFAULT NULL COMMENT '安装位置',
    `status`int(11) DEFAULT 0 COMMENT '状态，0-正常，转向架异常（1-温度异常，2-检测到异物，3-部件缺失），受电弓姿态异常（100-降弓，101-升弓），受电弓实体异常（201-受电弓燃弧、202-受电弓异物、203-受电弓变形、204-右弓角缺失、205-左弓角缺失），受电弓温度异常（300-受电弓温度异常，statusText字段补充温度范围）',
    `statusText` varchar(255) DEFAULT NULL COMMENT '状态描述',
    `description` varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `unique_name` (`name`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '监视物';

-- 日志 --
CREATE TABLE `log`  (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `type` int(11) NOT NULL COMMENT '日志类型：0-系统日志，1-操作日志',
    `userId` int(11) DEFAULT 0 COMMENT '用户ID，0-表示系统',
    `terminal` varchar(100) DEFAULT NULL COMMENT '操作终端（用户登录终端）',
    `title` varchar(100) NOT NULL COMMENT '标题',
    `content` varchar(255) NOT NULL COMMENT '日志内容',
    `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_title`(`title`) USING BTREE,
    INDEX `idx_type`(`type`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '日志';

-- 机车信息 --
CREATE TABLE `train`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `model` varchar(100) NOT NULL COMMENT '车型',
    `trainNo` varchar(100) NOT NULL COMMENT '车次',
    `name` varchar(255) NOT NULL COMMENT '名称',
    `carriageNum` int(11) DEFAULT 0 COMMENT '车厢数',
    `longitude` float DEFAULT NULL COMMENT '经度',
    `latitude` float DEFAULT NULL COMMENT '纬度',
    `description` varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `unique_trainNo` (`trainNo`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '机车信息';

--
-- Dumping data for table `train`
--

LOCK TABLES `train` WRITE;
/*!40000 ALTER TABLE `train` DISABLE KEYS */;
INSERT INTO `train` VALUES (1, '未定义', '未定义', '未定义', '0', '0.0', '0.0', '');
/*!40000 ALTER TABLE `train` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;