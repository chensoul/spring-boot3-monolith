CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `key` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `created_at` datetime NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime NOT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

CREATE INDEX user_is_active_idx ON user (`key`, is_active);

INSERT INTO user ( name, `key`, is_active, created_at, updated_at) VALUES ( 'test','123', true, NOW(), NOW());
