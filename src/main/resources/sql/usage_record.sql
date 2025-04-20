CREATE TABLE `usage_record` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `interview_id` VARCHAR ( 36 ) COMMENT '面试ID',
                                `uid` BIGINT NOT NULL COMMENT '用户唯一标识',
                                `use_type` INT NOT NULL COMMENT '使用类型：1-语音转文字',
                                `used_seconds` BIGINT COMMENT '使用时长（秒）',
                                `cost_in_cents` INT NOT NULL COMMENT '费用（分）',
                                `description` VARCHAR ( 500 ) COMMENT '描述',
                                `gmt_create` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `gmt_modify` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                PRIMARY KEY ( ` id ` ),
                                KEY ` idx_interview_id ` ( ` interview_id ` )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '使用记录表';