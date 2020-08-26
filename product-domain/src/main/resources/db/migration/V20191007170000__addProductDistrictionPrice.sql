alter table t_product add distribution_price decimal(10, 2) default 0.00 comment '分销价';
alter table t_product add presale integer not null default 0 comment '预售数量，如果大于0则为预售产品';
