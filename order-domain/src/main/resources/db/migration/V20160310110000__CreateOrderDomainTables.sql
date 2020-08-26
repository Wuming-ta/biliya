DROP TABLE IF EXISTS t_order_customer_service;
DROP TABLE IF EXISTS t_order_item;
DROP TABLE IF EXISTS t_order_process_log;
DROP TABLE IF EXISTS t_order;
DROP TABLE IF EXISTS t_shopping_cart;
DROP TABLE IF EXISTS t_express;
DROP TABLE IF EXISTS t_order_statistic;

CREATE TABLE IF NOT EXISTS t_order (
  id integer not null primary key auto_increment,
  user_id integer,
  order_number varchar(50),
  trade_number varchar(50),
  payment_type varchar(50),
  created_date datetime default null, /*创建时间*/
  pay_date datetime default null, /*支付时间*/
  confirm_date datetime default null, /*确认时间*/
  deliver_date datetime default null, /*开始发货时间*/
  delivered_date datetime default null, /*完成发货时间*/
  deal_date datetime default null, /*收货确认时间*/
  deliver_order_number varchar(50),
  status varchar(50),
  total_price decimal(10, 2) not null default 0.00,
  freight decimal(10, 2) not null default 0.00,
  description text,
  remark varchar(250),
  invoice integer default 0, /*是否开票，1是，0否*/
  invoice_title varchar(200),/*发票抬头*/
  receiving_time varchar(100), /*期望收货时间,比如周一至周五，周六日等*/
  zip char(6),
  contact_user varchar(50),
  phone varchar(50),
  province varchar(50),
  city varchar(50),
  district varchar(50),
  street varchar(100),
  detail varchar(200),
  cover varchar(200),
  express_number varchar(100), /*快递单号*/
  express_company varchar(100), /*快递公司*/
  express_code varchar(100), /*快递公司代码*/
  settled integer default 0, /*是否已分成*/
  previous_status varchar(50),
  is_deliver_reminder integer default 0, /*是否用户触发提醒发货*/
  is_deleted integer default 0, /*当用户删除CLOSED_CONFIRMED的订单时,不真实删除,而是设置该字段为1*/
  point_exchange_rate integer default 100, /*支付方式是积分支付时使用,把价钱用积分形式显示*/
  coupon_info varchar(250),
  marketing varchar(50), /*营销活动纪录*/
  marketing_id integer, /*假如提交的订单中，订单项的marketing_id不一样，则新建的t_order记录的marketing_id是随机选1个*/
  marketing_description varchar(250),
  foreign key (user_id) references t_user (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_order_item (
  id integer not null primary key auto_increment,
  order_id integer not null,
  product_id integer,
  product_name varchar(100),
  quantity integer not null default 1,
  price decimal(10, 2) not null default 0.00,
  final_price decimal(10, 2) not null default 0.00,
  status varchar(50),
  cost_price decimal(10, 2) not null default 0.00,
  cover varchar(200),
  partner_level_zone integer default null,
  product_specification_name varchar(200),
  product_specification_id integer,
  weight integer default 0,
  bulk integer default 0,
  barcode varchar(100), /*条码*/
  store_location varchar(200), /*库存地址*/
  marketing varchar(50), /*营销活动纪录*/
  marketing_id integer,
  marketing_description varchar(250),
  foreign key (order_id) references t_order (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_order_process_log (
  id integer primary key auto_increment,
  order_id integer not null,
  process_date timestamp null,
  content varchar(255),
  foreign key (order_id) references t_order(id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_shopping_cart (
  id integer not null primary key auto_increment,
  user_id integer,
  product_id integer,
  product_name varchar(100),
  cover varchar(200),
  quantity integer not null default 1,
  price decimal(10, 2) not null default 0.00,
  weight integer default 0,
  bulk integer default 0,
  created_date datetime default null,
  product_specification_id integer,
  product_specification_name varchar(200),
  fare_id integer,
  marketing varchar(50), /*营销活动纪录*/
  marketing_id integer,
  foreign key (fare_id) references t_fare_template (id) on delete cascade,
  foreign key (user_id) references t_user (id) on delete cascade,
  foreign key (product_id) references t_product (id) on delete cascade,
  foreign key (product_specification_id) references t_product_specification (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_express (
  id integer not null primary key auto_increment,
  code varchar(100) not null,
  name varchar(200) not null,
  enabled integer not null default 1,
  is_default integer not null default 0
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_order_customer_service (
  id integer not null primary key auto_increment,
  order_id integer not null,
  created_date datetime default null,
  express_number varchar(100), /*快递单号*/
  express_company varchar(100), /*快递公司*/
  express_code varchar(100), /*快递公司代码*/
  reason varchar(200), /*退款原因*/
  service_type varchar(50), /*退款类型: 退款, 退货*/
  status varchar(50),
  log text,
  images text, /*json text for image list*/
  refund_fee decimal(10, 2) default null, /*退款金额, 不能大于订单金额*/
  foreign key (order_id) references t_order (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_order_statistic (
    id integer not null primary key auto_increment,
    created_date datetime default null,
    sales_amount decimal(10, 2) not null default 0.00
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;
