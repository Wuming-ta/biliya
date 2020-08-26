DROP TABLE IF EXISTS t_physical_apply_tips;
DROP TABLE IF EXISTS t_physical_agent_bonus;
DROP TABLE IF EXISTS t_seller;
DROP TABLE IF EXISTS t_agent;
DROP TABLE IF EXISTS t_merchant_options;
DROP TABLE IF EXISTS t_pcd_qualify;
DROP TABLE IF EXISTS t_agent_pcd_qualify;
DROP TABLE IF EXISTS t_apply;
DROP TABLE IF EXISTS t_platform_seller;
DROP TABLE IF EXISTS t_seller_ancestor;
DROP TABLE IF EXISTS t_partner_level;
DROP TABLE IF EXISTS t_settlement_proportion;
DROP TABLE IF EXISTS t_cooperative_statistic;

/*合伙人级别定义*/
CREATE TABLE IF NOT EXISTS t_partner_level (
    id integer not null primary key auto_increment,
    name varchar(200),
    level integer,
    headcount_quota integer
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_seller (
    id integer not null primary key auto_increment,
    user_id integer not null,
    parent_id integer,
    partner_ship integer not null default 0,
    partner_id integer,
    seller_ship integer not null default 0,
    level integer default 1,
    partner_ship_time timestamp null,
    seller_ship_time timestamp null,
    partner_level_id integer default null,
    crown_ship integer not null default 0,
    crown_ship_temp integer default 0, /*是否是临时皇冠商。（1 临时 0 永久）线下被批准成为线下皇冠时，是一个临时皇冠商（不能查看
                                线下门店），到了指定时间如果完成批发额要求，则临时变成永久，如果不完成，则此字段不作更
                                改，撤销crown_ship，只看crown_ship字段，不看crown_ship_temp字段*/
    crown_ship_time timestamp null,
    crown_id integer,
    crown_apply_failure_times integer default 0,  /*线下申请成为线下皇冠，指定时间内没完成定额销售则撤销皇冠商资格，
                                                      每撤销1次，此值增加1，当此值>=3时，申请成为线下皇冠时将由自动审核
                                                      转为人工审核*/
    foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_seller_ancestor (
    seller_id integer not null,
    ancestor_id integer not null,
    level integer,
    foreign key (seller_id) references t_seller (id) on delete cascade,
    foreign key (ancestor_id) references t_seller (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


CREATE TABLE IF NOT EXISTS t_agent (
    id integer not null primary key auto_increment,
    user_id integer not null,
    foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_pcd_qualify (
    id integer not null primary key auto_increment,
    pcd_id integer not null,
    turnover_quota integer, /*每月最低营业额*/
    cash_deposit integer, /*保证金*/
    allow_inferior smallint default 1, /*允许下级代理*/
    physical_settlement_percentage integer, /*该地区的分成比例*/
    foreign key (pcd_id) references t_pcd (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_agent_pcd_qualify (
    agent_id integer not null,
    pcd_qualify_id integer not null,
    physical_settlement_percentage integer, /*该用户在该地区的分成比例*/
    foreign key (agent_id) references t_agent (id) on delete cascade,
    foreign key (pcd_qualify_id) references t_pcd_qualify (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


CREATE TABLE IF NOT EXISTS t_merchant_options (
    id integer not null primary key auto_increment,
    max_level integer not null,
    pool_priority integer not null
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_settlement_proportion (
    id integer not null primary key auto_increment,
    name varchar(50),
    type varchar(50),
    proportion text, /*利润分成, json format,eg:  {"percentage": true, "value": 10}, or 对于合伙人是固定金额,零元区/精品区/特价区 {"fixedvaule": true, "1": 2, "2": 4, "3": 5} */
    turnover_quota integer not null default 0, /*每月最低营业额*/
    level integer
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_apply (
    id integer not null primary key auto_increment,
    user_id integer,
    type varchar(50),
    status varchar(50),
    properties varchar(225),
    apply_date timestamp null,
    approve_date timestamp null,
    reject_date timestamp null,
    foreign key (user_id) references t_user (id) on delete cascade
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_platform_seller (
    seller_id integer not null,
    foreign key (seller_id) references t_seller (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_cooperative_statistic (
    id integer primary key auto_increment,
    statistic_date timestamp null,
    real_date timestamp null,
    seller_count integer default 0,
    agent_count integer default 0,
    partner_count integer default 0,
    crown_count integer default 0,
    physical_count integer default 0,
    customer_count integer default 0
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_physical_agent_bonus (
  id integer primary key auto_increment,
  pcd_id integer not null,
  min_amount decimal(15, 2)	, /*最小销售额*/
  max_amount decimal(15, 2), /*最大销售额*/
  percentage decimal(5,2), /*销售额在该区间的奖金比例*/
  foreign key (pcd_id) references t_pcd (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_physical_apply_tips (
 id integer not null primary key auto_increment,
 name varchar(100),
 type varchar(50),
 content text,
 created_date timestamp null,
 last_modified_date timestamp null,
 enabled integer default 0
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;


--3级分销
INSERT INTO t_merchant_options VALUES (1, 3, 1);

INSERT INTO t_partner_level (name, `level`, headcount_quota) VALUES ('一星经销商', 1, 500), ('二星经销商', 2, 1000), ('三星经销商', 3, 3000), ('四星经销商', 4, 900), ('五星经销商', 5, 27000), ('六星经销商', 6, NULL);

INSERT INTO t_settlement_proportion (name, type, proportion, `level`) VALUES
    ('平台', 'PLATFORM', '{"percentage": true, "value": 2.0}', 0),
    ('一星经销商', 'PARTNER', '{"fixedvalue": true, "1": 0.1, "2": 0.2, "3":0.3}', 1),
    ('二星经销商', 'PARTNER', '{"fixedvalue": true, "1": 0.15, "2": 0.25, "3":0.35}', 2),
    ('三星经销商', 'PARTNER', '{"fixedvalue": true, "1": 0.2, "2": 0.3, "3":0.4}', 3),
    ('四星经销商', 'PARTNER', '{"fixedvalue": true, "1": 0.25, "2": 0.35, "3":0.45}', 4),
    ('五星经销商', 'PARTNER', '{"fixedvalue": true, "1": 0.3, "2": 0.4, "3":0.5}', 5),
    ('六星经销商', 'PARTNER', '{"fixedvalue": true, "1": 0.5, "2": 0.5, "3":0.5}', 6),
    ('一级销售商', 'SELLER', '{"percentage": true, "value": 10.0}', 1),
    ('二级销售商', 'SELLER', '{"percentage": true, "value": 20.0}', 2),
    ('三级销售商', 'SELLER', '{"percentage": true, "value": 30.0}', 3),
    ('省级代理商', 'AGENT', '{"percentage": true, "value": 0.0}', 1),
    ('市级代理商', 'AGENT', '{"percentage": true, "value": 0.0}', 2),
    ('区级代理商', 'AGENT', '{"percentage": true, "value": 0.0}', 3),
    ('代理商自己购买', 'SELF', '{"percentage": true, "value": 10.0}', 0),
    ('皇冠', 'CROWN', '{"fixedvalue": true, "1": 0.5, "2": 0.5, "3":0.5}', 0),
    ('省级代理商', 'PHYSICAL_AGENT', '{"percentage": true, "value": 1.0}', 1),
    ('市级代理商', 'PHYSICAL_AGENT', '{"percentage": true, "value": 2.0}', 2),
    ('区级代理商', 'PHYSICAL_AGENT', '{"percentage": true, "value": 3.0}', 3);


--初始化 t_pcd_qualify表
insert into t_pcd_qualify (pcd_id) select id from t_pcd;

insert into t_physical_apply_tips (id, name, type, enabled) values (1, '皇冠经销商申请需知', 'CROWN', 1);
insert into t_physical_apply_tips  (id, name, type, enabled) values (2, '星级经销商申请需知', 'STAR', 1);
insert into t_physical_apply_tips (id, name, type, enabled) values (3, '系统公告', 'ANNOUNCE', 1);
