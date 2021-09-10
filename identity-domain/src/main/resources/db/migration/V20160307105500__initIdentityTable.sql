DROP TABLE IF EXISTS t_user_role;
DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS t_role;
DROP TABLE IF EXISTS t_permission;
DROP TABLE IF EXISTS t_permission_definition;
DROP TABLE IF EXISTS t_permission_group_definition;

CREATE TABLE IF NOT EXISTS t_user  (
  id integer not null primary key auto_increment,
  login_name varchar(150) unique,
  name varchar(255),
  real_name varchar(255),
  password varchar(255) not null,
  email varchar(150) unique,
  phone varchar(50) unique,
  weixin varchar(50) unique,
  register_date datetime default null,
  last_login_date datetime default null,
  salt varchar(255),
  status varchar(255),
  token_salt varchar(255),
  token_expired_date datetime default null,
  invitation_code varchar(50) unique,
  inviter_id integer default null,
  avatar varchar(255),
  sex integer default 0,
  birthday date default null,
  details text,
  uid varchar(20) unique,
  followed integer default 1,
  follow_time datetime default null,
  invitation_qrcode_url varchar(200), /*邀请二维码图片地址*/
  invitation_qrcode varchar(200), /*邀请二维码编码前数据*/
    `wx_unionid` varchar(50) DEFAULT NULL,
  `wxa_openid` varchar(50) DEFAULT NULL,
  `wxapp_openid` varchar(50) DEFAULT NULL,
  `app_user` int(11) DEFAULT '0',
  `wechat_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `grade` varchar(50) DEFAULT NULL,
  `contact_phone` varchar(50) DEFAULT NULL COMMENT '员工联系电话',
  `contact_wx_number` varchar(50) DEFAULT NULL COMMENT '员工联系微信号',
  `cabin_code` varchar(50) DEFAULT NULL COMMENT '关注小屋',
  `store_code` varchar(50) DEFAULT NULL COMMENT '关注门店',
  `assistant_code` varchar(50) DEFAULT NULL COMMENT '导购员',
  `wxa_invitation_qrcode_url` varchar(250) DEFAULT NULL COMMENT '小程序邀请二维码',
  `last_modified_time` datetime DEFAULT NULL,
  `org_id` bigint(20) DEFAULT '0' COMMENT '数据隔离字段',
  `org_name` varchar(80) DEFAULT NULL COMMENT '租户名称',
  `domain` varchar(255) DEFAULT NULL COMMENT '用户所属租户域名',
  foreign key (inviter_id) references t_user (id) on delete set null
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_role (
  id integer not null primary key auto_increment,
  description varchar(255),
  name varchar(150),
  system integer not null default 0
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_permission (
  id integer not null primary key auto_increment,
  identifier varchar(255),
  role_id integer not null,
  foreign key (role_id) references t_role (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_user_role (
  user_id integer not null,
  role_id integer not null,
  primary key(user_id, role_id),
  foreign key (user_id) references t_user (id) on delete cascade,
  foreign key (role_id) references t_role (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_permission_definition (
  identifier varchar(150) not null primary key,
  name varchar(255),
  description varchar(255)
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_permission_group_definition (
  identifier varchar(150) not null primary key,
  name varchar(255),
  description varchar(255),
  permissions text
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;
