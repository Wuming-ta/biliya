insert into t_wechat_message_type (id, name, display_name) values (200, 'coupon-dispatched', '优惠券发送通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(200, 'title', '标题'),
(200, 'message', '消息'),
(200, 'valid-date', '到期日'),
(200, 'remark', '备注');

insert into t_wechat_message_type (id, name, display_name) values (201, 'coupon-overdue', '优惠券即将到期通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(201, 'title', '标题'),
(201, 'message', '消息'),
(201, 'overdue-date', '到期日'),
(201, 'remark', '备注');




