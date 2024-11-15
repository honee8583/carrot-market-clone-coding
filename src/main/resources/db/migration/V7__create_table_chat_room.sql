CREATE TABLE `chat_room`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `room_num`     VARCHAR(255),
    `sender_id`      BIGINT,
    `receiver_id`    BIGINT,
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`receiver_id`) REFERENCES `members`(`id`),
    FOREIGN KEY (`sender_id`) REFERENCES `members`(`id`)
);