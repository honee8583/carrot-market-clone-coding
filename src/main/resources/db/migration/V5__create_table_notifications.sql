CREATE TABLE notifications
(
    `id`          BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `member_id`   BIGINT,
    `content`     VARCHAR(255) NOT NULL,
    `is_read`     BOOLEAN      NOT NULL,
    `type`        VARCHAR(10)  NOT NULL,
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
);
