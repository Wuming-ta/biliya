DROP TABLE IF EXISTS t_config;
DROP TABLE IF EXISTS t_config_group;

CREATE TABLE IF NOT EXISTS t_config_group (
    id integer not null primary key auto_increment,
    name varchar(50),
    protected integer default 0
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE IF NOT EXISTS t_config (
    id integer not null primary key auto_increment,
    user_id integer,
    group_id integer,
    name varchar(50),
    key_name varchar(50),
    value_type varchar(50),
    value varchar(250),
    type varchar(50),
    visible int(11),
    description varchar(225),
    readonly integer default 0,
    foreign key (group_id) references t_config_group(id) on delete cascade
) ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

insert into t_config_group (id, name) values (1, '商城配置');

--default setting
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description)
 values (1, '最迟可退货时间', 'mall.latest_return_time', 'int', '30', 'sys', 1, '单位为天'),
 (1, '分销商最低销售额度', 'mall.lowest_sales_quota', 'int', '1000', 'sys', 0, '注册不满100天，额度放宽'),
 (1, '返点级别', 'mall.rebates_level', 'int', '2', 'sys', 0, '级别等级相关'),
 (1, '积分兑换金额', 'mall.point_exchange_rate', 'int', '100', 'sys', 1, '1元等于多少积分'),
 (1, '包邮最低额度', 'mall.free_mail_minimum_amount', 'int', '998', 'sys', 1, ''),
 (1, '分销商提款条件', 'mall.drawing_conditions', 'int', '100', 'sys', 1, '拥金余额要不少于该数值'),
 (1, '自动确认收货期限', 'mall.auto_validation_receiving_deadline', 'int', '15', 'sys', 1, '如分销商不主动推迟日期'),
 (1, '支付超时时间', 'mall.pay_order_timeout', 'int', '12', 'sys', 1, '待支付的订单超过这个时间限制后自动关闭'),
 (1, '分销商申请自动审核开关', 'mall.seller_apply_auto_approve', 'boolean', 'true', 'sys', 1, '分销商申请自动审核开关'),
 (1, '新用户注册自动成为分销商开关', 'mall.seller_auto_sellership', 'boolean', 'true', 'sys', 1, '新用户注册自动成为分销商开关'),
 (1, '分销商达到人数自动升级为合伙人开关', 'mall.seller_auto_promote_to_partner', 'boolean', 'true', 'sys', 1, '分销商达到人数自动升级为合伙人开关'),
 (1, '产品售罄自动下架开关', 'mall.auto_offsell', 'boolean', 'false', 'sys', 1, '当产品售罄后,自动把该产品下架'),
 (1, '首页推荐产品轮询时间', 'mall.promoted_product_carousel', 'int', '60', 'sys', 1, '首页推荐产品轮询时间, 每过这个时间就轮换一批首页产品。单位分钟。最小30分钟。'),
 (1, '新建订单开关', 'mall.order_created_enable', 'boolean', 'true', 'sys', 1, '允许用户新建订单, 用于临时关闭下单功能。'),
 (1, '下单默认选中优惠券开关', 'mall.auto_select_coupon', 'boolean', 'true', 'sys', 1, '下单结算时, 如果用户有可用的优惠券, 默认选择一张优惠券进行结算。');
