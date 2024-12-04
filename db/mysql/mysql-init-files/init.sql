CREATE DATABASE IF NOT EXISTS `app_01` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs */ /*!80016 DEFAULT ENCRYPTION='N' */;

create user 'appuser'@'%' identified by 'pleasedontchange';
grant all privileges on app_01.* to 'appuser'@'%';
FLUSH PRIVILEGES;

use app_01;

CREATE TABLE `chat_message` (
                                `timestamp` DATETIME(6) NOT NULL,
                                `chat_room_id` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_as_cs',
                                `message` VARCHAR(1000) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_as_cs',
                                `sender` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_as_cs',
                                `type` TINYINT NULL DEFAULT NULL,
                                PRIMARY KEY (`timestamp`) USING BTREE,
                                CONSTRAINT `chat_message_chk_1` CHECK ((`type` between 0 and 2))
)
COLLATE='utf8mb4_0900_as_cs'
ENGINE=InnoDB
;

CREATE TABLE `chat_room` (
                             `room_id` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_0900_as_cs',
                             `inserted_datetime` DATETIME(6) NULL DEFAULT NULL,
                             `member_count` INT NOT NULL,
                             `name` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_as_cs',
                             `updated_datetime` DATETIME(6) NULL DEFAULT NULL,
                             PRIMARY KEY (`room_id`) USING BTREE
)
    COLLATE='utf8mb4_0900_as_cs'
ENGINE=InnoDB
;

CREATE TABLE `chat_room_member` (
                                    `member_id` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_as_cs',
                                    `inserted_datetime` DATETIME(6) NULL DEFAULT NULL,
                                    `status_type` TINYINT NULL DEFAULT NULL,
                                    `updated_datetime` DATETIME(6) NULL DEFAULT NULL,
                                    `room_id` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_as_cs',
                                    PRIMARY KEY (`member_id`, `room_id`) USING BTREE,
                                    INDEX `FKo2m0t6s23cq219t6be4udgfml` (`room_id`) USING BTREE,
                                    CONSTRAINT `FKo2m0t6s23cq219t6be4udgfml` FOREIGN KEY (`room_id`) REFERENCES `chat_room` (`room_id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
                                    CONSTRAINT `chat_room_member_chk_1` CHECK ((`status_type` between 0 and 1))
)
    COLLATE='utf8mb4_0900_as_cs'
ENGINE=InnoDB
;