alter table t_order_customer_service add supplementary_fee decimal(10, 2) default null comment '补款金额(用于换货单)';
alter table t_order_customer_service add store_id varchar(50) default null;
alter table t_order_customer_service add store_name varchar(50) default null;
alter table t_order_customer_service add store_user_id varchar(50) default null;
alter table t_order_customer_service add store_user_name varchar(50) default null;
alter table t_order_customer_service add service_number varchar(50) default null comment '售后单号';
alter table t_order_customer_service add result varchar(250) default null comment '处理结果';


/* 1. 对于退回项，每一个退回项都记录着退款金额（不一定等于购买时单价*数量），其关联的退货单记录着应退回的总额refund_fee
   2. 对于置换项，每一个置换项都不记录退款金额或补款金额，而用final_price记录此置换项需要付出多少钱购买。一张换货单的项目是
      由退回清单和置换清单组成，由 退回清单总价值 减去 置换清单总价值 就知道 该换货单是应该补交款还是退差价。补交款由售后单
      t_order_customer_service的refund_fee记录，退差价由supplementary_fee记录(refund_fee和supplementary_fee不可能同时不为null)
*/
CREATE TABLE IF NOT EXISTS t_order_customer_service_item ( /*退回项/置换项*/
  id integer not null primary key auto_increment,
  order_customer_service_id integer not null comment '售后单id',
  refund_fee decimal(10, 2) default null comment '退款金额(仅退回项使用)',
  type varchar(50) not null default 'RETURN' comment '项类型(RETURN 退回项 EXCHANGE 置换项)',

  /*字段同t_order_item(只用到部分字段)*/
  product_id integer not null comment '产品id',
  product_name varchar(100) comment '产品名称',
  quantity integer not null default 1 comment '退回项：退回数量/件 置换项：置换数量/件',
  price decimal(10, 2) not null default 0.00 comment '单价',
  final_price decimal(10, 2) not null default 0.00 comment '退回项：原支付金额 置换项：置换项金额',
  cost_price decimal(10, 2) not null default 0.00 comment '成本价',
  cover varchar(200) comment '产品封面',
  product_specification_name varchar(200) comment '产品规格名',
  product_specification_id integer comment '产品规格id',
  weight integer default 0 comment '重量',
  marketing varchar(50) comment '营销活动记录',
  marketing_id integer comment '营销活动id',
  marketing_description varchar(250) comment '营销活动描述',

  foreign key (order_customer_service_id) references t_order_customer_service (id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

alter table t_order add column refund_fee decimal(10, 2) not null default 0.00 comment '退货款金额';
alter table t_order add column supplementary_fee decimal(10, 2) not null default 0.00 comment '换货补差价金额';
alter table t_order add column origin_price decimal(10, 2) not null default 0.00 comment '原价';
alter table t_order add column coupon_price decimal(10, 2) not null default 0.00 comment '优惠券价钱';
alter table t_order add column credit_price decimal(10, 2) not null default 0.00 comment '积分抵扣价钱';
