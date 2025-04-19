CREATE TABLE `interview_info`
(
    `interview_id`         varchar(36)                                                   NOT NULL COMMENT '面试ID',
    `uid`                  varchar(36)                                                   NOT NULL COMMENT '用户ID',
    `interview_name`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '未命名面试' COMMENT '面试名称',
    `interview_language`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '中文' COMMENT '面试交流语言',
    `programming_language` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci           DEFAULT NULL COMMENT '编程语言',
    `position_info`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '职位信息',
    `job_requirements`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '招聘信息/工作要求',
    `extra_data`           varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT '{}' COMMENT '扩展字段，包含联网模式、语音触发、语音触发词、快捷键，回答风格等配置',
    `start_time`           timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '面试开始时间',
    `end_time`             timestamp NULL DEFAULT NULL COMMENT '面试结束时间',
    `status`               int                                                           NOT NULL DEFAULT '0' COMMENT '面试状态：0-待开始，1-进行中，2-已完成',
    `stt_total_seconds`    bigint                                                        NOT NULL DEFAULT '0' COMMENT '语音转文字总时长（单位：秒）',
    `stt_total_cost`       bigint                                                        NOT NULL DEFAULT '0' COMMENT '语音转文字费用（单位：分）',
    `ag_total_count`       int                                                           NOT NULL DEFAULT '0' COMMENT '生成答案调用次数',
    `ag_total_cost`        bigint                                                        NOT NULL DEFAULT '0' COMMENT '生成答案总费用（单位：分）',
    `version`              int                                                           NOT NULL DEFAULT '0' COMMENT '版本号',
    `gmt_create`           timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`           timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`interview_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='面试信息表';