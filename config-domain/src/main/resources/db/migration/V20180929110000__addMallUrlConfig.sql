
insert into t_config_group (id, name) values (20, '商城链接配置');
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
(20, '商城首页', 'mall.url.home', 'String', '', '', 1, '公众号前端商城首页链接地址'),
(20, '产品详情页', 'mall.url.product_detail', 'String', '/cosmetics/productDetails?id={0}', '', 1, '公众号前端产品详情页链接地址'),
(20, '我的订单页', 'mall.url.my_order', 'String', '/order/myOrders', '', 1, '公众号前端我的订单链接地址'),
(20, '订单详情页', 'mall.url.order_detail', 'String', '/order/myOrderDetail?id={0}', '', 1, '公众号前端订单详情页链接地址'),
(20, '个人中心页', 'mall.url.personal_center', 'String', '/personalCenter', '', 1, '公众号前端个人中心页链接地址'),
(20, '门店列表页', 'mall.url.store_list', 'String', '/skinHousekeeper/reservation', '', 1, '公众号前端门店列表页链接地址');


insert into t_config_group (id, name) values (21, '商城链接小程序页面配置');
insert into t_config (group_id, name, key_name, value_type, value, type, visible, description) values
(21, '商城首页', 'mall.wxa.url.home', 'String', '/pages/index/index', '', 1, '小程序首页页面路径');


