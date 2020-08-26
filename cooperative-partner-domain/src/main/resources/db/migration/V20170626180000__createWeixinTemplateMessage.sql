insert into t_wechat_message_type (id, name, display_name) values (300, 'temp-crown-approved', '成为临时皇冠商通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(300, 'title', '标题'),
(300, 'assignor', '授权人'),
(300, 'assignee', '被授权人'),
(300, 'status', '授权状态'),
(300, 'remark', '备注');
insert into t_wechat_message_type (id, name, display_name) values (301, 'physical-seller-approved', '成为星级经销商通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(301, 'title', '标题'),
(301, 'assignor', '授权人'),
(301, 'assignee', '被授权人'),
(301, 'status', '授权状态'),
(301, 'remark', '备注');
insert into t_wechat_message_type (id, name, display_name) values (302, 'temp-crown-resetted', '临时皇冠商撤销通知');
insert into t_wechat_message_type_prop (type_id, name, display_name) values
(302, 'title', '标题'),
(302, 'resetted-time', '时间'),
(302, 'reason', '原因'),
(302, 'remark', '备注');