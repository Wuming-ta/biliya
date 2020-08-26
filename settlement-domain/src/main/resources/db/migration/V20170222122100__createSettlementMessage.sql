insert into t_wechat_message_type (id, name, display_name) values (100, 'reward-cash-applying', '提现申请提交成功通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(100, 'title', '标题'),
(100, 'amount', '金额'),
(100, 'apply-time', '申请时间'),
(100, 'remark', '备注');

insert into t_wechat_message_type (id, name, display_name) values (101, 'reward-cash-handling', '提现申请处理中通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(101, 'title', '标题'),
(101, 'remark', '备注');

insert into t_wechat_message_type (id, name, display_name) values (102, 'reward-cash-rejected', '提现申请被拒绝通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(102, 'title', '标题'),
(102, 'rejected-time', '拒绝时间'),
(102, 'reason', '拒绝原因'),
(102, 'remark', '备注');

insert into t_wechat_message_type (id, name, display_name) values (103, 'reward-cash-completed', '成功提现通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(103, 'title', '标题'),
(103, 'amount', '金额'),
(103, 'completed-time', '提现成功时间'),
(103, 'remark', '备注');