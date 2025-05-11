CREATE TABLE `account_0` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sharding_key` tinyint NOT NULL COMMENT 'sharding_key',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `account_id` bigint DEFAULT NULL COMMENT '账户id = accountType + subType + userId',
  `account_type` tinyint NOT NULL DEFAULT '100' COMMENT '账户类型：100-用户、200-平台 ',
  `sub_type` tinyint NOT NULL COMMENT '子账户类型: 10-现金户（如充值的金额）、20-返现户 ',
  `balance` decimal(18,2) DEFAULT NULL COMMENT '用户余额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '账户状态:0-禁用，1-启用',
  `income_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '总收入',
  `expense_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '总支出',
  `credit_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '欠款金额',
  `freeze_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '冻结',
  `seq` bigint NOT NULL DEFAULT '0' COMMENT ' userId、account_type, seq建立唯一索引',
  `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_uat` (`user_id`,`account_type`,`sub_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1921514567781351427 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户账户表'