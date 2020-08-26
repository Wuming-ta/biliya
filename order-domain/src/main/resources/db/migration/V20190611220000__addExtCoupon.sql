ALTER TABLE t_order ADD COLUMN ext_coupon_id varchar(50) comment '第三方优惠券的优惠券ID';
ALTER TABLE t_order ADD COLUMN ext_user_type varchar(50) comment '第三方优惠券的用户类型';
ALTER TABLE t_order ADD COLUMN ext_coupon_type varchar(50) comment '第三方优惠券的优惠券类型';
ALTER TABLE t_order ADD COLUMN ext_discount integer comment '第三方优惠券的折扣率';
ALTER TABLE t_order ADD COLUMN ext_cuts integer comment '第三方优惠券的优惠';
