/*成交门店信息*/
alter table t_order add column store_id varchar(50) default NULL;
alter table t_order add column store_code varchar(50) default NULL;
alter table t_order add column store_name varchar(50) default NULL;
alter table t_order add column store_cover varchar(250) default NULL;
alter table t_order add column store_address varchar(250) default NULL;
/*关注门店信息*/
alter table t_order add column followed_store_id varchar(50) default NULL;
alter table t_order add column followed_store_code varchar(50) default NULL;
alter table t_order add column followed_store_name varchar(50) default NULL;
alter table t_order add column followed_store_cover varchar(250) default NULL;
/*绑定小屋信息*/
alter table t_order add column binding_store_id varchar(50) default NULL;
alter table t_order add column binding_store_code varchar(50) default NULL;
alter table t_order add column binding_store_name varchar(50) default NULL;
alter table t_order add column binding_store_cover varchar(250) default NULL;
/*门店导购员信息*/
alter table t_order add column store_guide_user_id varchar(50) default NULL;
alter table t_order add column store_guide_user_code varchar(50) default NULL;
alter table t_order add column store_guide_user_name varchar(50) default NULL;
/*门店结算员信息*/
alter table t_order add column store_user_id varchar(50) default NULL;
alter table t_order add column store_user_code varchar(50) default NULL;
alter table t_order add column store_user_name varchar(50) default NULL;
/*上级邀请人*/
alter table t_order add column inviter_user_id varchar(50) default NULL;
alter table t_order add column inviter_user_name varchar(50) default NULL;

alter table t_order add column type varchar(50) default 'ORDER';

/*会员积分支付*/
alter table t_order add column pay_credit int default 0;

/*订单配送方式,快递，自提*/
alter table t_order add column delivery_type varchar(50) DEFAULT 'EXPRESS';
/*订单来源：公众号-WPA(Wechat public account) 小程序-MINI_PROGRAM 手机应用程序-APP 其他-OTHER*/
alter table t_order add column origin varchar(50) DEFAULT NULL;

alter table t_order add column comment_id varchar(50) DEFAULT NULL comment '评价ID';
