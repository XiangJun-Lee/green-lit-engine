CREATE TABLE `interview_info`
(
    `interview_id`         varchar(36)   NOT NULL COMMENT '面试ID',
    `uid`                  varchar(36)   NOT NULL COMMENT '用户ID',
    `interview_name`       varchar(100)  NOT NULL COMMENT '面试名称',
    `interview_language`   varchar(20)   NOT NULL COMMENT '面试交流语言',
    `programming_language` varchar(20)   NOT NULL COMMENT '编程语言',
    `position_info`        varchar(255)  NOT NULL COMMENT '职位信息',
    `job_requirements`     text          NOT NULL COMMENT '招聘信息/工作要求',
    `extra_data`           varchar(1000) NOT NULL DEFAULT '{}' COMMENT '扩展字段，包含联网模式、语音触发、语音触发词、快捷键，回答风格等配置',
    `start_time`           timestamp     NOT NULL COMMENT '面试开始时间',
    `end_time`             timestamp     NOT NULL COMMENT '面试结束时间',
    `STATUS`               int           NOT NULL DEFAULT '0' COMMENT '面试状态：0-待开始，1-进行中，2-已完成',
    `gmt_create`           timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`           timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`interview_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='面试信息表';