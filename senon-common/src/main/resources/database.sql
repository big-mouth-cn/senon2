SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent` bigint(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` timestamp NULL DEFAULT NULL,
  `file_type` smallint(6) NOT NULL DEFAULT '0' COMMENT '0:文件,1:文件夹',
  PRIMARY KEY (`id`),
  KEY `parent` (`parent`),
  CONSTRAINT `file_ibfk_1` FOREIGN KEY (`parent`) REFERENCES `file` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` VALUES (DEFAULT, null, '调度系统', CURRENT_TIME, CURRENT_TIME, '1');

-- ----------------------------
-- Table structure for job
-- ----------------------------
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `cron` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `dependencies` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `job_type` int(11) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `schedule_status` int(11) DEFAULT NULL,
  `schedule_type` int(11) DEFAULT NULL,
  `script` text COLLATE utf8_bin,
  `file_id` bigint(20) NOT NULL,
  `allocation_type` int(255) DEFAULT '0' COMMENT '0:自动 ，1：指定目标',
  `execution_machine` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `file_id` (`file_id`),
  CONSTRAINT `job_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of job
-- ----------------------------

-- ----------------------------
-- Table structure for job_history
-- ----------------------------
DROP TABLE IF EXISTS `job_history`;
CREATE TABLE `job_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8_bin,
  `end_time` datetime DEFAULT NULL,
  `result` smallint(6) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `trigger_type` int(11) DEFAULT NULL,
  `job_id` bigint(20) NOT NULL,
  `execution_machine` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `job_id` (`job_id`),
  CONSTRAINT `job_history_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=142464 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of job_history
-- ----------------------------

-- ----------------------------
-- Table structure for workers
-- ----------------------------
DROP TABLE IF EXISTS `workers`;
CREATE TABLE `workers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `worker_base_url` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of workers
-- ----------------------------
INSERT INTO `workers` VALUES ('1', 'http://localhost:9082', 'LOCALHOST', null);
