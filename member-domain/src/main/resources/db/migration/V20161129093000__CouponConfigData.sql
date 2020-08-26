
insert into t_config_group (id, name) values (10, '优惠券配置');

-- default setting
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
(10, '分享链接有限时间', 'coupon.share_link_valid_days', 'int', '30', 'sys', 1, '单位为天'),
(10, '获得红包时间', 'coupon.pocket_time', 'int', '24', 'sys', 1, '单位为小时, 在这个时间内可以获得红包, 与 获得红包的订单数 配合使用'),
(10, '获得红包的订单数', 'coupon.pocket_order_count', 'int', '2', 'sys', 1, '超过这个限制的订单不发红包, 与 获得红包时间 配合使用'),
(10, '分享红包标题', 'coupon.pocket_share_title', 'String', '快来领取十美优品专享红包', 'sys', 1, '分享红包到朋友圈或发给朋友时显示的标题'),
(10, '分享红包描述', 'coupon.pocket_share_desc', 'String', '快来领取十美优品专享红包', 'sys', 1, '分享红包到朋友圈或发给朋友时显示的描述'),
(10, '快过期优惠券通知时间', 'coupon.overdue_time_interval', 'int', '24', '', 1, '单位为小时，优惠券将要过期时，发送微信消息通知用户');

