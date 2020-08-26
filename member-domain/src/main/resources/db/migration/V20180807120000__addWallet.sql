DROP TABLE IF EXISTS t_wallet_charge;
DROP TABLE IF EXISTS t_wallet_history;
DROP TABLE IF EXISTS t_wallet;

CREATE TABLE IF NOT EXISTS t_wallet (
  id integer not null primary key auto_increment,
  user_id integer not null,
  accumulative_amount decimal(10, 2) not null default 0 comment '实际累计储值',
  accumulative_gift_amount decimal(10, 2) not null default 0 comment '赠送累计储值',
  balance decimal(10, 2) not null default 0 comment '实际余额',
  gift_balance decimal(10, 2) not null default 0 comment '赠送余额',
  password varchar(250) default null comment '支付密码',
  salt varchar(250) default null comment '密码盐',
  foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wallet_charge (
  id integer not null primary key auto_increment,
  wallet_id integer not null,
  amount decimal(10, 2) not null default 0 comment '充值金额',
  gift_amount decimal(10, 2) not null default 0 comment '赠送金额',
  status varchar(50) comment '待支付/已支付/关闭',
  created_time datetime default null,
  paid_time datetime default null,
  out_trade_no varchar(50),
  pay_type varchar(50),
  description varchar(250),
  foreign key (wallet_id) references t_wallet (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


CREATE TABLE IF NOT EXISTS t_wallet_history (
  id integer not null primary key auto_increment,
  wallet_id integer not null,
  created_time datetime default null,
  type varchar(50) not null comment '类型：支付/充值/提现',
  amount decimal(10, 2) not null default 0 comment '发生金额',
  gift_amount decimal(10, 2) not null default 0 comment '赠送储值，充值时用',
  balance decimal(10, 2) not null default 0 comment '实际余额',
  gift_balance decimal(10, 2) not null default 0 comment '赠送余额',
  note varchar(255) default null,
  foreign key (wallet_id) references t_wallet (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;
