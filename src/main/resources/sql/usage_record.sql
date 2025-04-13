CREATE TABLE usage_record
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id     BIGINT      NOT NULL COMMENT '关联的面试 ID',
    user_id          BIGINT      NOT NULL COMMENT '操作人 ID',
    usage_type       VARCHAR(20) NOT NULL COMMENT '使用类型：STT / AG',
    duration_seconds INT                  DEFAULT 0 COMMENT '仅 STT 有效，单位秒',
    cost_in_cents    BIGINT      NOT NULL COMMENT '本次使用的费用，单位：分',
    description      VARCHAR(255) COMMENT '可选说明',
    gmt_create       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modify       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
);