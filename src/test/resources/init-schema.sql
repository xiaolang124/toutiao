DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `password` varchar(64) NOT NULL,
  `salt` varchar(64) DEFAULT NULL,
  `head_url` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(64) NOT NULL,
  `link` varchar(128) DEFAULT NULL,
  `image` varchar(128) DEFAULT NULL,
  `like_count` int(11) DEFAULT NULL,
  `comment_count` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `login_ticket`;
CREATE TABLE `login_ticket` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `ticket` VARCHAR(45) NOT NULL,
  `expired` DATETIME NOT NULL,
  `status` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ticket_UNIQUE` (`ticket` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `content` TEXT NOT NULL,
  `user_id` INT NOT NULL,
  `entity_id` INT NOT NULL,
  `entity_type` INT NOT NULL,
  `create_date` DATETIME NOT NULL,
  `status` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `entity_index` (`entity_id` ASC, `entity_type` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `from_id` INT NULL,
  `to_id` INT NULL,
  `content` TEXT NULL,
  `create_date` DATETIME NULL,
  `has_read` INT NULL,
  `conversation_id` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `conversation_index` (`conversation_id` ASC),
  INDEX `create_date` (`create_date` ASC))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;