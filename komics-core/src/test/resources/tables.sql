CREATE TABLE `USER` (
  id       BIGINT(11) PRIMARY KEY AUTO_INCREMENT
  COMMENT '用户ID',
  version  BIGINT(10) DEFAULT '1'                NOT NULL
  COMMENT '数据版本号',
  created  TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL
  COMMENT '创建时间',
  modified TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL
  COMMENT '最新修改时间',
  username VARCHAR(32)                           NOT NULL
  COMMENT '用户名',
  password VARCHAR(32)                           NOT NULL
  COMMENT '密码，MD5',
  mobile   VARCHAR(15)                           NOT NULL
  COMMENT '手机号码',
  email    VARCHAR(32)                           NOT NULL
  COMMENT '电子邮箱',
  status   VARCHAR(20)                           NOT NULL
  COMMENT '用户状态'
)
  CHARACTER SET 'UTF8';