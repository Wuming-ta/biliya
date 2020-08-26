insert into t_wechat_message_type (id, name, display_name) values (1, 'order-created', '成功下单通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(1, 'title', '标题'),
(1, 'order-number', '订单号'),
(1, 'order-price', '订单金额'),
(1, 'contact-user', '收货人'),
(1, 'contact-address', '收货地址'),
(1, 'contact-phone', '联系电话'),
(1, 'remark', '备注');

insert into t_wechat_message_type (id, name, display_name) values (2, 'order-refunded', '订单退款通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(2, 'title', '标题'),
(2, 'order-number', '订单号'),
(2, 'order-price', '订单金额'),
(2, 'refunded-time', '退款时间'),
(2, 'remark', '备注');

insert into t_wechat_message_type (id, name, display_name) values (3, 'order-canceled', '取消订单通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(3, 'title', '标题'),
(3, 'order-number', '订单号'),
(3, 'order-price', '订单金额'),
(3, 'canceled-time', '取消时间'),
(3, 'remark', '备注');

insert into t_wechat_message_type (id, name, display_name) values (4, 'order-pay-timeout', '订单支付超时通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(4, 'title', '标题'),
(4, 'order-number', '订单号'),
(4, 'remark', '备注');

insert into t_wechat_message_type (id, name, display_name) values (5, 'order-delivering', '订单发货通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(5, 'title', '标题'),
(5, 'order-number', '订单号'),
(5, 'express-company', '快递公司'),
(5, 'express-number', '快递单号'),
(5, 'contact-user', '收件人'),
(5, 'remark', '备注');

insert into t_wechat_message_type (id, name, display_name) values(6, 'order-service-created', '订单退款申请通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
 (6, 'title', '标题'),
 (6, 'order-number', '订单号'),
 (6, 'order-price', '退款金额'),
 (6, 'remark', '备注');

