CREATE TABLE `interview_record`
(
    `id`           bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `interview_id` varchar(36) NOT NULL COMMENT '关联的面试ID',
    `question`     text        NOT NULL COMMENT '面试问题',
    `answer`       text        NOT NULL COMMENT '面试答案',
    `gmt_create`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='面试记录流水表';