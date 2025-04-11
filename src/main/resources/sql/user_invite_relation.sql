CREATE TABLE `user_invite_relation`
(
    `id`          bigint    NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `inviter_uid` bigint    NOT NULL COMMENT '邀请人ID',
    `invitee_uid` bigint    NOT NULL COMMENT '被邀请人ID',
    `gmt_create`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_invitee` (`invitee_uid`),
    KEY           `idx_inviter` (`inviter_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户邀请关系表';