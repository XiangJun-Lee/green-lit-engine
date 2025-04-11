CREATE TABLE `user_info`
(
    `uid`         bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
    `phone`       varchar(20)     NOT NULL COMMENT '用户手机号',
    `email`       varchar(100)    NOT NULL DEFAULT '' COMMENT '用户邮箱',
    `password`    varchar(255)    NOT NULL COMMENT '加密后的密码',
    `status`      int             NOT NULL DEFAULT '1' COMMENT '是否激活（1: 正常, 2:冻结, 3:风控, -1: 注销）',
    `nick_name`   varchar(255)    NOT NULL DEFAULT '' COMMENT '用户昵称',
    `user_role`   int unsigned    NOT NULL DEFAULT '0' COMMENT '用户角色（0: 普通用户, 999: 管理员等）',
    `resume_text` text COMMENT '简历',
    `client_ip`   varchar(255)    NOT NULL DEFAULT '' COMMENT '客户端局域网ip',
    `version`     int             NOT NULL DEFAULT '0' COMMENT '版本号',
    `gmt_create`  timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`  timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `invite_code` varchar(8)               DEFAULT NULL COMMENT '邀请码',
    PRIMARY KEY (`uid`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_email` (`email`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户信息表';