alter table t_product add sku_id varchar(50);
alter table t_product add sku_name varchar(50);
alter table t_product add sku_code varchar(50);
alter table t_product add bar_code varchar(255);
alter table t_product add mid integer comment '商家id';

alter table t_product_specification add sku_id varchar(50);
alter table t_product_specification add sku_name varchar(50);
alter table t_product_specification add sku_code varchar(50);
alter table t_product_specification add bar_code varchar(255);

alter table t_product modify stock_balance integer default 0;
alter table t_product_specification modify stock_balance integer default 0;

alter table t_product add allow_coupon integer default 0 comment '优惠活动-优惠券';
alter table t_product add credit integer default 0 comment '优惠活动-可用积分';
alter table t_product add is_virtual integer default 0 comment '是否虚拟产品';
alter table t_product add required_participate_exam integer default 0 comment '是否需要做了检测才可以购买';
