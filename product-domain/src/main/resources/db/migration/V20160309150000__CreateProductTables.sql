DROP TABLE IF EXISTS t_product_image;
DROP TABLE IF EXISTS t_product_category_property;
DROP TABLE IF EXISTS t_product_category;
DROP TABLE IF EXISTS t_product_property;
DROP TABLE IF EXISTS t_product;
DROP TABLE IF EXISTS t_product_brand;
DROP TABLE IF EXISTS t_product_favorite;
DROP TABLE IF EXISTS t_product_hit_word;
DROP TABLE IF EXISTS t_product_specification;
DROP TABLE IF EXISTS t_incl_postage_proviso;
DROP TABLE IF EXISTS t_carry_mode;
DROP TABLE IF EXISTS t_fare_template;
DROP TABLE IF EXISTS t_product_purchase_strategy;
DROP TABLE IF EXISTS t_product_purchase_strategy_item;
DROP TABLE IF EXISTS t_product_purchase_strategy_relation;

/*运费模板*/
/*模板表*/
CREATE TABLE IF NOT EXISTS t_fare_template (
    id integer not null primary key auto_increment,
    name varchar(50) not null , /*模板名称*/
    shop_addr varchar(100) null, /*宝贝地址*/
    dispatch_time varchar(20) null, /*发货时间*/
    is_incl_postage integer default 1, /*是否包邮*/
    valuation_model integer not null, /*计价方式(1:按件 2:按重量 3:按体积)*/
    is_incl_postage_by_if integer default 0,  /*是否指定条件包邮*/
    last_modified_date datetime null,
    title varchar(50),
    content text,
    message_format varchar(200)
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

/*包邮条件表*/
CREATE TABLE IF NOT EXISTS t_incl_postage_proviso (
    id integer not null primary key auto_increment,
    fare_id integer not null, /*模板表外键*/
    region varchar(200) null, /*包邮地区(存id,格式为'省-市-区',以'|'分隔)*/
    piece_no integer null, /*包邮件数*/
    weight_no integer null, /*包邮重量*/
    bulk_no integer null, /*包邮体积*/
    amount decimal(18,2) null, /*包邮金额*/
    carry_way integer default 0, /*运送方式（0:快递, 1: EMS, 2: 平邮）*/
    type integer not null, /*包邮类型 : 重量,件数,金额,体积*/
    foreign key (fare_id) references t_fare_template (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

/*运送方式表*/
CREATE TABLE IF NOT EXISTS t_carry_mode (
    id integer not null primary key auto_increment,
    fare_id integer not null, /*模板表外键*/
    region varchar(200) null, /*运送地区(存id,格式为'省-市-区',以'|'分隔)*/
    first_piece integer null, /*首件数量*/
    first_weight integer null, /*首重重量*/
    first_bulk integer null, /*首体积大小*/
    first_amount decimal(18,2) not null, /*首费*/
    second_piece integer null, /*续件*/
    second_weight integer null, /*续重*/
    second_bulk integer null, /*续体积*/
    second_amount decimal(18,2) not null, /*续费*/
    carry_way integer default 0, /*运送方式（0:快递, 1: EMS, 2: 平邮）*/
    is_default integer default 0, /*是否默认的运送方式*/
    foreign key (fare_id) references t_fare_template (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


CREATE TABLE IF NOT EXISTS t_product_category (
  id integer not null primary key auto_increment,
  parent_id integer,
  name varchar(100) not null,
  description text,
  cover varchar(200), /*封面icon*/
  sort_order integer default 100,
  promoted integer default 1,
  visible integer default 1,
  promoted_product_count integer default 3,
  wholesale integer default 0, /*是否是批发类别。
                                  注意：新建批发活动选择产品的时候，根据产品类别来filter出产品让用户选择
                                  ，即用户也可以将一种零售产品用于批发。另外，也可以将一种批发产品用于零售*/
  is_show_products integer default 1, /* 1 点击此类别的链接时,是显示类别和该类别下所有产品； 0 显示类别和该类别下第1个产品 */
  foreign key (parent_id) references t_product_category (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_category_property (
  id integer not null primary key auto_increment,
  category_id integer not null,
  display_name varchar(100) not null,
  value_type varchar(50), /*string, boolean, int, date, etc*/
  input_type varchar(50), /*checkbox, radio, input, textarea, select, etc*/
  candidate_values text, /*json string*/
  default_value text,
  is_required integer default 0,
  sort_order integer default 1,
  foreign key (category_id) references t_product_category (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_brand (
    id integer primary key auto_increment,
    name varchar(50) not null,
    description text,
    logo varchar(255)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product (
  id integer not null primary key auto_increment,
  category_id integer not null,
  brand_id integer,
  name varchar(100) not null,
  short_name varchar(100) not null,
  cover varchar(200), /*封面icon, 从多个封面中选中第一张*/
  stock_balance integer not null default 0, /*库存量*/
  sales integer not null default 0, /*销量*/
  status varchar(50) not null,
  created_date datetime default null,
  last_modified_date datetime default null,
  unit varchar(50),
  price decimal(10, 2) not null default 0.00,
  cost_price decimal(10, 2) not null default 0.00,
  suggested_price decimal(10, 2) not null default 0.00, /*建议售价*/
  promoted integer not null default 0,
  freight decimal(10, 2) not null default 0.00, /*运费*/
  free_shipping integer not null default 0, /*是否包邮：0－否， 1－是*/
  sort_order integer default 100,
  partner_level_zone integer default null,
  view_count bigint default 0,
  fare_id integer,
  barcode varchar(100), /*条码*/
  store_location varchar(200), /*库存地址*/
  weight integer default 0, /*重量*/
  bulk integer default 0, /*体积*/
  foreign key (category_id) references t_product_category (id) on delete cascade,
  foreign key (fare_id) references t_fare_template (id) on delete set null,
  foreign key (brand_id) references t_product_brand (id) on delete set null
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_description (
    id integer not null primary key auto_increment,
    product_id integer not null,
    description text,
    foreign key (product_id) references t_product (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_image (
  id integer not null primary key auto_increment,
  product_id integer not null,
  type integer not null default 0, /*0: 封面, 1: 详情图片*/
  url varchar(200) not null,
  sort_order int default 1,
  foreign key (product_id) references t_product (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_favorite (
  id integer not null primary key auto_increment,
  user_id integer not null,
  product_id integer not null,
  collect_date datetime default null,
  foreign key (user_id) references t_user (id) on delete cascade,
  foreign key (product_id) references t_product (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;


CREATE TABLE IF NOT EXISTS t_product_property (
  id integer not null primary key auto_increment,
  product_id integer not null,
  property_id integer not null,
  property_value varchar(200),
  foreign key (product_id) references t_product (id) on delete cascade,
  foreign key (property_id) references t_product_category_property (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_hit_word (
  id integer not null primary key auto_increment,
  name varchar(100),
  hit integer default 0
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_specification (
  id integer not null primary key auto_increment,
  product_id integer not null,
  name varchar(100),
  stock_balance integer not null default 0,
  price decimal(10, 2) not null default 0.00,
  cost_price decimal(10, 2) not null default 0.00,
  suggested_price decimal(10, 2) not null default 0.00,
  weight integer default 0,
  bulk integer default 0,
  foreign key (product_id) references t_product (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_purchase_strategy (
  id integer not null primary key auto_increment,
  name varchar(200) not null,
  description text
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_purchase_strategy_item (
  id integer not null primary key auto_increment,
  strategy_id integer not null,
  name varchar(200) not null,
  operator varchar(10) not null,
  sort_order integer,
  param text, /* {"defined_quantity": 2} */
  foreign key (strategy_id) references t_product_purchase_strategy (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_product_purchase_strategy_relation (
  product_id integer not null,
  strategy_id integer not null,
  primary key(product_id, strategy_id),
  foreign key (product_id) references t_product (id) on delete cascade,
  foreign key (strategy_id) references t_product_purchase_strategy (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;
