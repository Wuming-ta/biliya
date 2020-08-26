DROP TABLE IF EXISTS t_order_item_reward;
DROP TABLE IF EXISTS t_reward_cash;
DROP TABLE IF EXISTS t_owner_balance;
DROP TABLE IF EXISTS t_withdraw_account;

CREATE TABLE IF NOT EXISTS t_order_item_reward (
    id integer not null primary key auto_increment,
    order_id integer,
    order_number varchar(200),
    order_total_price decimal(10,2),
    order_created_time datetime default null,
    order_paid_time datetime default null,
    order_item_id integer,
    order_profit decimal(10, 2) default 0.00,
    percent integer default null,
    reward decimal(10, 2) not null default 0.00,
    owner_id integer not null,
    level integer, /*分销商级别或合伙人级别,结合type使用*/
    state varchar(50),
    type varchar(50),
    created_time datetime default null,
    settled_time datetime default null,
    withdrawn_time datetime default null,
    order_user_name varchar(100),
    payment_type varchar(50),
    point_exchange_rate integer default 100, /*仅用于前端展示，结算到余额表时不使用此字段*/
    foreign key (order_id) references t_order (id) on delete set null,
    foreign key (owner_id) references t_user (id) on delete cascade,
    foreign key (order_item_id) references t_order_item (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_reward_cash (
    id integer not null primary key auto_increment,
    owner_id integer not null,
    cash decimal(10, 2) not null default 0.00,
    apply_time datetime default null,
    reject_time datetime default null,
    complete_time datetime default null,
    status varchar(50),
    account_type varchar(50),
    account_number varchar(50),
    account_name varchar(50),
    bank_name varchar(50),
    note varchar(255),
    foreign key (owner_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_owner_balance (
    id integer not null primary key auto_increment,
    user_id integer not null,
    balance decimal(10, 2) not null default 0.00,
    version integer not null default 0,/*多线程更新检查用*/
    foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_withdraw_account (
    id integer not null primary key auto_increment,
    user_id integer not null,
    type varchar(50), /*wechat,alipay,bank*/
    bank_name varchar(50), /*available when the type is bank*/
    account varchar(50),
    owner_name varchar(50),
    foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;
