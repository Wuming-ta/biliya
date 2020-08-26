DROP TABLE IF EXISTS t_marketing_config;
DROP TABLE IF EXISTS t_wholesale_member;
DROP TABLE IF EXISTS t_wholesale_pricing;
DROP TABLE IF EXISTS t_wholesale_category;
DROP TABLE IF EXISTS t_wholesale;
DROP TABLE IF EXISTS t_piece_group_purchase_member;
DROP TABLE IF EXISTS t_piece_group_purchase_master;
DROP TABLE IF EXISTS t_piece_group_purchase_pricing;
DROP TABLE IF EXISTS t_piece_group_purchase;

CREATE TABLE IF NOT EXISTS t_piece_group_purchase (
 id integer primary key auto_increment,
 marketing_name varchar(200) not null,
 marketing_short_name varchar(50),
 min_participator_count integer default 2,
 duration integer not null, /* seconds */
 price decimal(10,2) not null,
 suggested_price decimal(10,2) not null,
 sale integer default 0,
 description text,
 cover varchar(255), /* 活动封面 */
 product_id integer not null,
 status varchar(50) not null,
 master_free integer default 0, /* 团长免单标记, 0 非免单活动, 1 可以使用免单优惠券的活动, 默认0 */
 free_shipping integer default 0, /* 0 包邮, 1 根据产品定义的邮费计算。 默认0 */
 coupon_usage integer default 0, /* 0 不能用优惠券；1 可以用专用优惠券；2 可以用系统优惠券. 默认0 */
 payment_type varchar(50) not null,
 coupon_strategy_service_name varchar(50), /* 拼团成功时的赠送免单优惠券策略所对应的service的类名*/
 sort_order integer default 100,
 foreign key (product_id) references t_product (id) on delete cascade
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_piece_group_purchase_pricing (
 id integer primary key auto_increment,
 piece_group_purchase_id integer not null,
 participator_count integer not null,
 price decimal(10,2) not null,
 foreign key (piece_group_purchase_id) references t_piece_group_purchase (id) on delete cascade,
 unique (piece_group_purchase_id,participator_count)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_piece_group_purchase_master (
 id integer primary key auto_increment,
 piece_group_purchase_id integer not null,
 user_id integer not null,
 start_time timestamp null,
 end_time timestamp null,
 status varchar(50) not null,
 promoted integer default 1, /* 推荐进入活动详情页方便其他用户参团, 0 不推荐, 1 推荐, 当免单开团的时候该团为不推荐,需要团长自己拉人参团 */
 foreign key (piece_group_purchase_id) references t_piece_group_purchase (id) on delete cascade,
 foreign key (user_id) references t_user (id) on delete cascade
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_piece_group_purchase_member (
 id integer primary key auto_increment,
 master_id integer,
 user_id integer not null,
 created_time timestamp null,
 status varchar(50) not null,
 order_number varchar(50),
 foreign key (master_id) references t_piece_group_purchase_master (id) on delete cascade,
 foreign key (user_id) references t_user (id) on delete cascade
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wholesale_category (
 id integer primary key auto_increment,
 name varchar(50) not null,
 sort_order integer default 100
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wholesale (
    id integer primary key auto_increment,
    marketing_name varchar(200) not null,
    marketing_short_name varchar(50),
    category_id integer,
    product_id integer not null,
    sale integer default 0,
    cover varchar(255),
    status varchar(50) not null,
    description text,
    settlement_proportion integer, /*批发分成比例*/
    agent_proportion decimal(10,2), /*代理分成比例*/
    sort_order integer default 100,
    unit varchar(50),
    foreign key (category_id) references t_wholesale_category (id) on delete set null,
    foreign key (product_id) references t_product (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wholesale_pricing (
    id integer primary key auto_increment,
    wholesale_id integer not null,
    region text,
    price decimal(10,2) not null,
    suggested_retail_price decimal(10,2),  /* 建议零售价 */
    suggested_wholesale_price decimal(10,2), /*星级经销价*/
    is_default integer default 0,
    enabled integer not null default 1,
    foreign key (wholesale_id) references t_wholesale (id) on delete cascade
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_wholesale_member (
 id integer primary key auto_increment,
 wholesale_id integer not null,
 user_id integer not null,
 created_time timestamp null,
 status varchar(50) not null,
 user_name varchar(50),
 user_real_name varchar(50),
 user_phone varchar(50),
 order_number varchar(50) not null,
 total_price decimal(10,2),
 foreign key (user_id) references t_user (id) on delete cascade
)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_marketing_config (
 id integer primary key auto_increment,
 type varchar(50) not null,
 enabled integer not null default 1
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;