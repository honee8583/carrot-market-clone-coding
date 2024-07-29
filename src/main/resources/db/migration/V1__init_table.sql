CREATE TABLE `members`
(
    `id`               BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `nickname`         VARCHAR(255),
    `profile_url`      VARCHAR(255),
    `town`             VARCHAR(255),
    `withdraw`         TINYINT(1),
    `is_authenticated` TINYINT(1),
    `create_date`      DATETIME,
    `update_date`      DATETIME
);

CREATE TABLE `boards`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `member_id`   BIGINT NOT NULL,
    `title`       VARCHAR(255),
    `method`      VARCHAR(255),
    `price`       INT,
    `suggest`     TINYINT(1),
    `description` TEXT,
    `place`       TEXT,
    `visit`       INT,
    `hide`        TINYINT(1),
    `status`      VARCHAR(30),
    `tmp`         TINYINT(1),
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`member_id`) REFERENCES `members`(`id`)
);

CREATE TABLE `categories`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`        VARCHAR(255),
    `create_date` DATETIME,
    `update_date` DATETIME
);

CREATE TABLE `board_categories`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `board_id`    BIGINT NOT NULL,
    `category_id` BIGINT NOT NULL,
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`board_id`) REFERENCES `boards`(`id`),
    FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`)
);

CREATE TABLE `words`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `member_id`   BIGINT NOT NULL,
    `word`        TEXT,
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`member_id`) REFERENCES `members`(`id`)
);

CREATE TABLE `board_pictures`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `board_id`    BIGINT NOT NULL,
    `picture_url` VARCHAR(255),
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`board_id`) REFERENCES `boards`(`id`)
);

CREATE TABLE `board_likes`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `member_id`   BIGINT NOT NULL,
    `board_id`    BIGINT NOT NULL,
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`member_id`) REFERENCES `members`(`id`),
    FOREIGN KEY (`board_id`) REFERENCES `boards`(`id`)
);

CREATE TABLE `notices`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `member_id`   BIGINT NOT NULL,
    `content`     TEXT,
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`member_id`) REFERENCES `members`(`id`)
);

CREATE TABLE `keywords`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `member_id`   BIGINT NOT NULL,
    `category_id` BIGINT NOT NULL,
    `name`        VARCHAR(255),
    `minPrice`    INT,
    `maxPrice`    INT,
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`member_id`) REFERENCES `members`(`id`),
    FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`)
);

CREATE TABLE `life_boards`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `member_id`   BIGINT NOT NULL,
    `title`       VARCHAR(255),
    `content`     TEXT,
    `tmp`         TINYINT(1),
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`member_id`) REFERENCES `members`(`id`)
);

CREATE TABLE `subjects`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`        VARCHAR(255),
    `create_date` DATETIME,
    `update_date` DATETIME
);

CREATE TABLE `life_board_subjects`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `board_id`    BIGINT NOT NULL,
    `subject_id`  BIGINT NOT NULL,
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`board_id`) REFERENCES `life_boards`(`id`),
    FOREIGN KEY (`subject_id`) REFERENCES `subjects`(`id`)
);

CREATE TABLE `life_board_likes`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `member_id`   BIGINT NOT NULL,
    `board_id`    BIGINT NOT NULL,
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`member_id`) REFERENCES `members`(`id`),
    FOREIGN KEY (`board_id`) REFERENCES `life_boards`(`id`)
);

CREATE TABLE `life_board_images`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `board_id`    BIGINT NOT NULL,
    `picture_url` VARCHAR(255),
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`board_id`) REFERENCES `life_boards`(`id`)
);

CREATE TABLE `except_keywords`
(
    `id`          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `keyword_id`  BIGINT NOT NULL,
    `name`        VARCHAR(255),
    `create_date` DATETIME,
    `update_date` DATETIME,
    FOREIGN KEY (`keyword_id`) REFERENCES `keywords`(`id`)
);
