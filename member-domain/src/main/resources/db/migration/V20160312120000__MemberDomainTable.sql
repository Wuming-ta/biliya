DROP TABLE IF EXISTS t_member_level;
DROP TABLE IF EXISTS t_member_ext;
DROP TABLE IF EXISTS t_member_point_history;
DROP TABLE IF EXISTS t_coupon_overdue;
DROP TABLE IF EXISTS t_coupon;
DROP TABLE IF EXISTS t_coupon_type;
DROP TABLE IF EXISTS t_coupon_template;
DROP TABLE IF EXISTS t_coupon_strategy;
DROP TABLE IF EXISTS t_coupon_share;
DROP TABLE IF EXISTS t_coupon_taken_record;
DROP TABLE IF EXISTS t_contact;
DROP TABLE IF EXISTS t_user_coupon_notify;
DROP TABLE IF EXISTS t_coupon_statistic;

CREATE TABLE IF NOT EXISTS t_member_level (
  id integer not null primary key auto_increment,
  name varchar(100) not null unique,
  description varchar(200),
  point integer not null
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_member_ext (
  id integer not null primary key auto_increment,
  user_id integer not null,
  level_id integer not null,
  name varchar(100),
  sex integer,
  birthday varchar(50),
  mobile varchar(20),
  address varchar(200),
  description varchar(200),
  point integer not null default 0,
  foreign key (user_id) references t_user (id) on delete cascade,
  foreign key (level_id) references t_member_level (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_member_point_history (
  id integer not null primary key auto_increment,
  member_ext_id integer not null,
  description varchar(100),
  point integer not null,
  changed_point integer not null,
  changed_date datetime default null,
  foreign key (member_ext_id) references t_member_ext (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_coupon_template(
  id integer not null primary key auto_increment,
  name varchar(200) not null unique,
  type varchar(50),
  is_limited integer,
  is_discount integer,
  cond text
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_coupon_type (
  id integer not null primary key auto_increment,
  product_id integer, /*产品型优惠劵才用到*/
  name varchar(200) not null,
  type varchar(50), /*优惠劵类型*/
  is_limited integer, /*是否属于满多少就XX类型的优惠劵*/
  auto_give integer default 0,
  up_to integer, /*满多少*/
  display_name varchar(200),
  money integer default 0, /*代金额*/
  discount integer default 0, /*折扣*/
  description text,
  cond text, /*使用条件*/
  valid_days integer, /*有效时间多少天*/
  template text,
  code varchar(200) unique,
  enabled integer default 1
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_coupon (
  id integer not null primary key auto_increment,
  user_id integer,
  name varchar(200) not null,
  auto_give integer default 0,
  type varchar(50),
  code varchar(200) unique,
  display_name varchar(200),
  money integer default 0,
  discount integer default 0,
  description text,
  cond text,
  created_date datetime default null,
  valid_date datetime default null,
  last_modified_date datetime default null,
  status varchar(50) not null,
  attribute text,
  foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_coupon_strategy (
  id integer not null primary key auto_increment,
  name varchar(200) not null,
  type varchar(50),
  random_number integer default 0 /*从该策略随机选择几个优惠券类型进行配送,如果为0则不随机*/
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_coupon_strategy_item (
  strategy_id integer not null,
  coupon_type_id integer not null,
  primary key (strategy_id, coupon_type_id),
  foreign key (strategy_id) references t_coupon_strategy (id) on delete cascade,
  foreign key (coupon_type_id) references t_coupon_type (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_coupon_share (
  id integer not null primary key auto_increment,
  user_id integer not null,
  code varchar(200) not null unique,
  order_number varchar(200) unique,
  type varchar(50) not null default 'ORDER',
  share_date datetime default null,
  valid_date datetime default null,
  foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_coupon_taken_record (
  share_id integer not null,
  user_id integer not null,
  created_date datetime default null,
  coupon_value integer default 0,
  message varchar(200),
  primary key (share_id, user_id),
  foreign key (share_id) references t_coupon_share (id) on delete cascade,
  foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


CREATE TABLE IF NOT EXISTS t_contact (
  id integer not null primary key auto_increment,
  user_id integer not null,
  zip char(6),
  contact_user varchar(50),
  phone varchar(50),
  province varchar(50),
  city varchar(50),
  district varchar(50),
  street varchar(100),
  street_number varchar(50),
  detail varchar(200),
  is_default integer not null default 0,
  foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_user_coupon_notify (
    id integer primary key auto_increment,
    user_id integer not null unique,
    is_notified integer not null default 0,
    coupon_value integer default 0,
    coupon_count integer default 0,
    notify_date datetime default null,
    foreign key (user_id) references t_user (id) on delete cascade
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_coupon_overdue (
    id integer primary key auto_increment,
    user_id integer not null,
    coupon_id integer not null,
    end_time timestamp ,
    foreign key (user_id) references t_user (id) on delete cascade,
    foreign key (coupon_id) references t_coupon (id) on delete cascade
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_coupon_statistic (
    id integer primary key auto_increment,
    statistic_date timestamp null,
    real_date timestamp null,
    grant_count integer	 default 0,
    used_count integer default 0,
    overdue_count integer default 0,
    given_by_register_count integer default 0,
    taken_by_link_count integer default 0,
    system_given_count integer default 0
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

-- coupon type template
INSERT INTO t_coupon_template (id, name, type, is_limited, is_discount, cond) VALUES (1, '限制型产品代金券', 'PRODUCT', 1, 0, '
<rule-set name="getFinalPrice" >
    <mvel-rule id="step1" multipleTimes="false" exclusive="true" valid="true">
        <condition><![CDATA[productId==#id#&&totalPrice>=#totalPrice#]]></condition>
        <action><![CDATA[finalPrice=totalPrice-#money#]]></action>
    </mvel-rule>
</rule-set>
');

INSERT INTO t_coupon_template (id, name, type, is_limited, is_discount, cond) VALUES (2, '无限制型产品代金券', 'PRODUCT', 0, 0, '
<rule-set name="getFinalPrice" >
    <mvel-rule id="step1" multipleTimes="false" exclusive="true" valid="true">
        <condition><![CDATA[productId==#id#]]></condition>
        <action><![CDATA[finalPrice=totalPrice-#money#]]></action>
    </mvel-rule>
</rule-set>
');

INSERT INTO t_coupon_template (id, name, type, is_limited, is_discount, cond) VALUES (3, '限制型产品折扣券', 'PRODUCT', 1, 1, '
<rule-set name="getFinalPrice" >
    <mvel-rule id="step1" multipleTimes="false" exclusive="true" valid="true">
        <condition><![CDATA[productId==#id#&&totalPrice>=#totalPrice#]]></condition>
        <action><![CDATA[finalPrice=totalPrice*#discount#/100]]></action>
    </mvel-rule>
</rule-set>
');

INSERT INTO t_coupon_template (id, name, type, is_limited, is_discount, cond) VALUES (4, '无限制型产品折扣券', 'PRODUCT', 0, 1, '
<rule-set name="getFinalPrice" >
    <mvel-rule id="step1" multipleTimes="false" exclusive="true" valid="true">
        <condition><![CDATA[productId==#id#]]></condition>
        <action><![CDATA[finalPrice=totalPrice*#discount#/100]]></action>
    </mvel-rule>
</rule-set>
');


INSERT INTO t_coupon_template (id, name, type, is_limited, is_discount, cond) VALUES (5, '限制型订单代金券', 'ORDER', 1, 0, '
<rule-set name="getFinalPrice" >
    <mvel-rule id="step1" multipleTimes="false" exclusive="true" valid="true">
        <condition><![CDATA[totalPrice>=#totalPrice#]]></condition>
        <action><![CDATA[finalPrice=totalPrice-#money#]]></action>
    </mvel-rule>
</rule-set>
');

INSERT INTO t_coupon_template (id, name, type, is_limited, is_discount, cond) VALUES (6, '无限制型订单代金券', 'ORDER', 0, 0, '
<rule-set name="getFinalPrice" >
    <mvel-rule id="step1" multipleTimes="false" exclusive="true" valid="true">
        <condition><![CDATA[true]]></condition>
        <action><![CDATA[finalPrice=totalPrice-#money#]]></action>
    </mvel-rule>
</rule-set>
');

INSERT INTO t_coupon_template (id, name, type, is_limited, is_discount, cond) VALUES (7, '限制型订单折扣券', 'ORDER', 1, 1,'
<rule-set name="getFinalPrice" >
    <mvel-rule id="step1" multipleTimes="false" exclusive="true" valid="true">
        <condition><![CDATA[totalPrice>=#totalPrice#]]></condition>
        <action><![CDATA[finalPrice=totalPrice*#discount#/100]]></action>
    </mvel-rule>
</rule-set>
');

INSERT INTO t_coupon_template (id, name, type, is_limited, is_discount, cond) VALUES (8, '无限制型订单折扣券', 'ORDER', 0, 1, '
<rule-set name="getFinalPrice" >
    <mvel-rule id="step1" multipleTimes="false" exclusive="true" valid="true">
        <condition><![CDATA[true]]></condition>
        <action><![CDATA[finalPrice=totalPrice*#discount#/100]]></action>
    </mvel-rule>
</rule-set>
');

--default member level
INSERT INTO t_member_level (id, name, point) VALUES (1, 'Level 1', 1000),(2, 'Level 2', 2000),(3, 'Level 3', 5000),(4, 'Level 4', 10000);

