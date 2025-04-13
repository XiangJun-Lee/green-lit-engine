CREATE TABLE `usage_record`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `interview_id`  varchar(36)  NOT NULL COMMENT '面试ID',
    `use_type`      int          NOT NULL COMMENT '使用类型：1-语音转文字',
    `used_seconds`  bigint       NOT NULL COMMENT '使用时长（秒）',
    `cost_in_cents` int          NOT NULL COMMENT '费用（分）',
    `created_at`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_interview_id` (`interview_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='使用记录表';